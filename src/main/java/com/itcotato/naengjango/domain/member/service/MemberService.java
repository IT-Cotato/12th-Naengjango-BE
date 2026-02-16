package com.itcotato.naengjango.domain.member.service;

import com.itcotato.naengjango.domain.member.dto.AgreementRequestDto;
import com.itcotato.naengjango.domain.member.dto.AgreementResponseDto;
import com.itcotato.naengjango.domain.member.dto.MemberRequestDTO;
import com.itcotato.naengjango.domain.member.dto.MyPageDto;
import com.itcotato.naengjango.domain.member.entity.Agreement;
import com.itcotato.naengjango.domain.member.entity.FixedExpenditure;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.entity.MemberAgreement;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import com.itcotato.naengjango.domain.member.exception.MemberException;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final SmsService smsService;

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
                        Agreement agreement = agreementRepository.findById(agreementId)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약관 ID: " + agreementId));
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
                member.getRole().name(),
                member.getCreatedAt()
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

    /**
     * 이용약관 조회 (비로그인 허용 가능)
     *
     * 구현 방식 옵션:
     * 1) DB에 정책 테이블을 두고 최신 버전 조회 (운영에 가장 적합)
     * 2) application.yml 등에 박아두고 내려주기 (초기엔 편하지만 운영/버전 관리 어려움)
     * 3) URL만 내려주고 프론트에서 WebView로 오픈 (서버 구현 가장 단순)
     *
     * 현재는 "스켈레톤" 형태로 응답만 구성해둠.
     * 실제 구현 시 PolicyRepository/PolicyEntity를 만들거나, 설정값 로딩으로 교체하면 됨.
     */
    @Transactional(readOnly = true)
    public MyPageDto.PolicyResponse getTermsPolicy() {
        // TODO: 정책 소스(DB/Config/URL)에서 불러오도록 교체
        return MyPageDto.PolicyResponse.builder()
                .title("이용약관")
                .version("v1.0")
                .contentType(MyPageDto.PolicyContentType.MARKDOWN)
                .content("이용약관 내용")
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 개인정보 처리방침 조회 (비로그인 허용 가능)
     */
    @Transactional(readOnly = true)
    public MyPageDto.PolicyResponse getPrivacyPolicy() {
        // TODO: 정책 소스(DB/Config/URL)에서 불러오도록 교체
        return MyPageDto.PolicyResponse.builder()
                .title("개인정보 처리 방침")
                .version("v1.0")
                .contentType(MyPageDto.PolicyContentType.MARKDOWN)
                .content("개인정보 처리 방침 내용")
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * FAQ 목록 조회 (비로그인 허용 가능)
     *
     * - category, keyword, page, size 를 받아서 목록 형태로 반환
     * - 실제 구현 시 FaqEntity/FaqRepository 필요
     */
    @Transactional(readOnly = true)
    public MyPageDto.FaqListResponse getFaqs(String category, String keyword, int page, int size) {
        // TODO: FAQ 테이블/레포지토리 구현 후 교체
        // 임시로 빈 리스트 반환 (스켈레톤)
        return MyPageDto.FaqListResponse.builder()
                .items(List.of())
                .page(page)
                .size(size)
                .totalElements(0L)
                .totalPages(0)
                .build();
    }

    /**
     * FAQ 상세 조회 (비로그인 허용 가능)
     *
     * - faqId로 질문/답변 조회
     */
    @Transactional(readOnly = true)
    public MyPageDto.FaqDetailResponse getFaqDetail(Long faqId) {
        // TODO: FAQ 테이블/레포지토리 구현 후 교체
        return MyPageDto.FaqDetailResponse.builder()
                .faqId(faqId)
                .category("기타")
                .question("자주 묻는 질문?")
                .answer("답변 내용")
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * 문의 등록 (로그인 사용자)
     *
     * - InquiryEntity/InquiryRepository 구현 필요
     * - 최소 필드 추천: memberId, title, content, status, answer, createdAt, answeredAt
     */
    @Transactional
    public MyPageDto.InquiryCreateResponse createInquiry(Long memberId, MyPageDto.InquiryCreateRequest request) {
        // TODO: Inquiry 저장 로직으로 교체
        // 예: Inquiry inquiry = inquiryRepository.save(...)
        Long generatedId = 1L;

        return MyPageDto.InquiryCreateResponse.builder()
                .inquiryId(generatedId)
                .status(MyPageDto.InquiryStatus.RECEIVED)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * 내 문의 목록 조회 (로그인 사용자)
     */
    @Transactional(readOnly = true)
    public MyPageDto.InquiryListResponse getMyInquiries(Long memberId, int page, int size) {
        // TODO: inquiryRepository.findByMemberId(...) + pageable 적용
        return MyPageDto.InquiryListResponse.builder()
                .items(List.of())
                .page(page)
                .size(size)
                .totalElements(0L)
                .totalPages(0)
                .build();
    }

    /**
     * 내 문의 상세 조회 (로그인 사용자)
     *
     * - memberId로 소유권 검증(다른 사람 문의 조회 방지)
     */
    @Transactional(readOnly = true)
    public MyPageDto.InquiryDetailResponse getMyInquiryDetail(Long memberId, Long inquiryId) {
        // TODO: inquiryRepository.findById(inquiryId) 후 memberId 일치 검증
        return MyPageDto.InquiryDetailResponse.builder()
                .inquiryId(inquiryId)
                .title("문의 제목")
                .content("문의 내용")
                .status(MyPageDto.InquiryStatus.RECEIVED)
                .answer(null)
                .createdAt(LocalDateTime.now())
                .answeredAt(null)
                .build();
    }

    @Transactional
    public MyPageDto.WithdrawResponse withdraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 멱등 처리: 이미 탈퇴면 OK로 돌려도 됨(앱 UX 안정)
        if (member.isDeleted()) {
            return MyPageDto.WithdrawResponse.builder()
                    .message("이미 탈퇴 처리된 계정입니다.")
                    .build();
        }
        member.withdraw();

        // (선택) Redis/JWT 블랙리스트 처리 등 토큰 무효화 정책이 있으면 여기서
        // redisTemplate.opsForValue().set("jwt:blacklist:" + token, "true", ...)

        return MyPageDto.WithdrawResponse.builder()
                .message("탈퇴 처리 완료")
                .build();
    }

    // 고정지출 수정
    @Transactional
    public MyPageDto.FixedExpendituresResponse updateFixedExpense(Long memberId, MyPageDto.UpdateFixedExpendituresRequest request) {
        // 멤버 존재 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 1) 기존 고정지출 삭제 (전체 교체)
        fixedExpenditureRepository.deleteByMemberId(memberId);

        // 2) 새 고정지출 저장
        List<FixedExpenditure> newItems = request.items().stream()
                .map(dto -> FixedExpenditure.builder()
                        .item(dto.item())
                        .amount(dto.amount())
                        .member(member)
                        .build())
                .toList();

        fixedExpenditureRepository.saveAll(newItems);

        // 3) 응답 구성
        List<MyPageDto.FixedExpenditureItem> responseItems = newItems.stream()
                .map(e -> new MyPageDto.FixedExpenditureItem(e.getItem(), e.getAmount()))
                .toList();

        return MyPageDto.FixedExpendituresResponse.builder()
                .items(responseItems)
                .build();
    }

    @Transactional(readOnly = true)
    public MyPageDto.FixedExpendituresResponse getFixedExpenses(Long memberId) {
        // 멤버 존재 확인
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        List<MyPageDto.FixedExpenditureItem> items = fixedExpenditureRepository.findByMemberId(memberId).stream()
                .map(e -> new MyPageDto.FixedExpenditureItem(e.getItem(), e.getAmount()))
                .toList();

        return MyPageDto.FixedExpendituresResponse.builder()
                .items(items)
                .build();
    }

    // 비밀번호 변경 관련
    @Transactional
    public void changePassword(Long memberId, MyPageDto.PasswordChangeRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        // 새 비밀번호 확인 일치
        if (!request.newPassword().equals(request.newPasswordConfirm())) {
            throw new IllegalArgumentException("New password confirmation does not match");
        }

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.currentPassword(), member.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // 저장 (암호화)
        String encoded = passwordEncoder.encode(request.newPassword());
        member.updatePassword(encoded);
    }


    private void validateSmsVerification(String phoneNumber) {
        String isVerified = redisTemplate.opsForValue().get(VERIFIED_PREFIX + phoneNumber);
        if (isVerified == null || !isVerified.equals("true")) {
            throw new GeneralException(SmsErrorCode.SMS_VERIFY_EXPIRED);
        }
    }

    /**
     * 전화번호 변경 로직
     * 1) SMS 인증 검증 (인증번호 일치 + 만료 여부)
     * 2) 전화번호 중복 체크 (본인 제외)
     * 3) 업데이트
     */
    @Transactional
    public void updatePhoneNumber(Member member, String phoneNumber, String verifyCode) {

        // 1. SMS 인증 검증
        String result = smsService.verifyCode(phoneNumber, verifyCode);

        if (!"SUCCESS".equals(result)) {
            throw new MemberException(SmsErrorCode.SMS_BAD_REQUEST);
        }

        // 2. 전화번호 중복 체크 (본인 제외)
        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new MemberException(MemberErrorCode.MEMBER_PHONE_ALREADY_EXISTS);
        }

        // 3. 업데이트
        member.updatePhoneNumber(phoneNumber);
    }

    @Transactional
    public void agreeAgreements(
            Member member,
            AgreementRequestDto.AgreeRequest request
    ) {

        Map<Long, Boolean> requestMap = request.agreements()
                .stream()
                .collect(Collectors.toMap(
                        AgreementRequestDto.AgreementItem::agreementId,
                        AgreementRequestDto.AgreementItem::agreed
                ));

        List<Agreement> agreements = agreementRepository.findAll();

        for (Agreement agreement : agreements) {

            boolean agreed = requestMap.getOrDefault(
                    agreement.getAgreementId(),
                    false
            );

            // 필수 약관 체크
            if (agreement.required() && !agreed) {
                throw new IllegalArgumentException("필수 약관 미동의");
            }

            MemberAgreement memberAgreement =
                    memberAgreementRepository
                            .findByMemberAndAgreement(member, agreement)
                            .orElseGet(() ->
                                    MemberAgreement.create(member, agreement)
                            );

            if (agreed) {
                memberAgreement.agree();
            } else {
                memberAgreement.withdraw();
            }

            memberAgreementRepository.save(memberAgreement);
        }
    }
}
