package com.itcotato.naengjango.domain.member.service;

import com.itcotato.naengjango.domain.member.dto.MemberRequestDTO;
import com.itcotato.naengjango.domain.member.dto.MyPageDto;
import com.itcotato.naengjango.domain.member.entity.Agreement;
import com.itcotato.naengjango.domain.member.entity.FixedExpenditure;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.entity.MemberAgreement;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import com.itcotato.naengjango.domain.member.exception.code.MemberErrorCode;
import com.itcotato.naengjango.domain.member.exception.code.SmsErrorCode;
import com.itcotato.naengjango.domain.member.repository.AgreementRepository;
import com.itcotato.naengjango.domain.member.repository.FixedExpenditureRepository;
import com.itcotato.naengjango.domain.member.repository.MemberAgreementRepository;
import com.itcotato.naengjango.domain.member.repository.MemberRepository;
import com.itcotato.naengjango.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final FixedExpenditureRepository fixedExpenditureRepository;
    private final AgreementRepository agreementRepository;
    private final MemberAgreementRepository memberAgreementRepository;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final String VERIFIED_PREFIX = "sms:verified:";

    /**
     * 사용자 회원가입 관련 로직을 처리하는 서비스
     * 아이디 중복 확인, 사용자 데이터 저장 담당
     */

    /**
     * 아이디 중복 확인
     * 중복이면 true, 사용 가능하면 false 반환
     */

    public boolean isLoginIdDuplicate(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }

    /**
     * 일반 회원가입
     */
    public void signup(MemberRequestDTO.SignupDTO request) {
        // 1. 휴대폰 인증 상태 유효 여부 확인 (Redis에 저장한 Prefix 확인해서 15분이 안 지났는지 확인)
        validateSmsVerification(request.getPhoneNumber());

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 유저 엔티티 생성 및 저장
        Member member = Member.builder()
                .loginId(request.getLoginId())
                .password(encodedPassword)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .budget(request.getBudget())
                .socialType(SocialType.LOCAL)
                .build();

        memberRepository.save(member);

        saveCommonMemberData(member, request.getAgreedAgreementIds(), request.getFixedExpenditures());

        // Redis 내 인증 정보 삭제
        redisTemplate.delete(VERIFIED_PREFIX + request.getPhoneNumber());
    }

    /**
     * 구글(소셜) 회원가입
     */
    public void signupSocial(MemberRequestDTO.SocialSignupDTO request) {
        Member member = memberRepository.findBySocialTypeAndSocialId(
                request.getSocialType(),
                request.getSocialId()
        ).orElseThrow(() ->
                new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND)
        );

        // 휴대폰 인증 확인
        validateSmsVerification(request.getPhoneNumber());

        // 이미 가입 완료된 경우 방어
        if (member.getPhoneNumber() != null) {
            throw new GeneralException(MemberErrorCode.ALREADY_SOCIAL_REGISTERED);
        }

        // 추가 정보 세팅
        member.updatePhoneNumber(request.getPhoneNumber());
        member.updateBudget(request.getBudget());

        saveCommonMemberData(
                member,
                request.getAgreedAgreementIds(),
                request.getFixedExpenditures()
        );

        redisTemplate.delete(VERIFIED_PREFIX + request.getPhoneNumber());
    }

    /**
     * 약관 동의 및 고정 지출 (일반/구글 회원가입 공통)
     */
    private void saveCommonMemberData(Member member, List<Long> agreementIds, List<MemberRequestDTO.FixedExpenditureDTO> expenditures) {
        // // 동의한 약관 ID 리스트를 순회하며 UserAgreement 생성
        if (agreementIds != null && !agreementIds.isEmpty()) {
            List<MemberAgreement> memberAgreements = agreementIds.stream()
                    .map(agreementId -> {
                        Agreement agreement = agreementRepository.getReferenceById(agreementId);
                        return MemberAgreement.builder()
                                .member(member)
                                .agreement(agreement)
                                .isAgreed(true)
                                .build();
                    })
                    .toList();
            memberAgreementRepository.saveAll(memberAgreements);
        }

        // 고정 지출 저장
        if (expenditures != null && !expenditures.isEmpty()) {
            List<FixedExpenditure> fixedExpenditures = expenditures.stream()
                    .map(dto -> FixedExpenditure.builder()
                            .item(dto.getItem())
                            .amount(dto.getAmount())
                            .member(member)
                            .build())
                    .toList();
            fixedExpenditureRepository.saveAll(fixedExpenditures);
        }
    }

    @Transactional(readOnly = true)
    public MyPageDto.MeResponse getMe(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return new MyPageDto.MeResponse(
                member.getId(),
                member.getName(),
                member.getLoginId(),
                member.getPhoneNumber(),
                member.getBudget(),
                member.getSocialType().name(),
                member.getRole().name()
        );
    }

    @Transactional(readOnly = true)
    public MyPageDto.BudgetResponse getBudget(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        return new MyPageDto.BudgetResponse(member.getBudget());
    }

    @Transactional
    public MyPageDto.BudgetResponse updateBudget(Long memberId, MyPageDto.UpdateBudgetRequest req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.updateBudget(req.budget());
        return new MyPageDto.BudgetResponse(member.getBudget());
    }

    private void validateSmsVerification(String phoneNumber) {
        String isVerified = redisTemplate.opsForValue().get(VERIFIED_PREFIX + phoneNumber);
        if (isVerified == null || !isVerified.equals("true")) {
            throw new GeneralException(SmsErrorCode.SMS_VERIFY_EXPIRED);
        }
    }

}
