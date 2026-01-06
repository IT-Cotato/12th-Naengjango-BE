package com.itcotato.naengjango.domain.user.service;

import com.itcotato.naengjango.domain.user.dto.UserRequestDTO;
import com.itcotato.naengjango.domain.user.entity.Agreement;
import com.itcotato.naengjango.domain.user.entity.FixedExpenditure;
import com.itcotato.naengjango.domain.user.entity.User;
import com.itcotato.naengjango.domain.user.entity.UserAgreement;
import com.itcotato.naengjango.domain.user.exception.code.SmsErrorCode;
import com.itcotato.naengjango.domain.user.repository.AgreementRepository;
import com.itcotato.naengjango.domain.user.repository.FixedExpenditureRepository;
import com.itcotato.naengjango.domain.user.repository.UserAgreementRepository;
import com.itcotato.naengjango.domain.user.repository.UserRepository;
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
public class UserService {
    private final UserRepository userRepository;
    private final FixedExpenditureRepository fixedExpenditureRepository;
    private final AgreementRepository agreementRepository;
    private final UserAgreementRepository userAgreementRepository;
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
        return userRepository.existsByLoginId(loginId);
    }

    /**
     * 회원가입 시 회원 정보 저장
     * 휴대폰 인증 완료 여부 확인 후 회원가입
     */
    public void signup(UserRequestDTO.SignupDTO request) {
        // 1. 휴대폰 인증 상태 유효 여부 확인 (Redis에 저장한 Prefix 확인해서 15분이 안 지났는지 확인)
        String isVerified = redisTemplate.opsForValue().get(VERIFIED_PREFIX + request.getPhoneNumber());
        if (isVerified == null || !isVerified.equals("true")) {
            throw new GeneralException(SmsErrorCode.SMS_VERIFY_EXPIRED);
        }

        // 2. 비밀번호 암호화 처리
        // 사용자가 입력한 평문 비밀번호를 해시화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 3. 유저 엔티티 생성 및 저장
        User user = User.builder()
                .loginId(request.getLoginId())
                .password(encodedPassword)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .budget(request.getBudget())
                .socialType(User.SocialType.GENERAL)
                .build();

        userRepository.save(user);

        // 4. 약관 동의 저장
        // 동의한 약관 ID 리스트를 순회하며 UserAgreement 생성
        if (request.getAgreedAgreementIds() != null && !request.getAgreedAgreementIds().isEmpty()) {
            List<UserAgreement> userAgreements = request.getAgreedAgreementIds().stream()
                    .map(agreementId -> {
                        Agreement agreement = agreementRepository.getReferenceById(agreementId);

                        return UserAgreement.builder()
                                .user(user)
                                .agreement(agreement)
                                .isAgreed(true) // ★ 핵심: DB의 tinyint(1) NO 설정을 만족시키기 위해 true(1) 주입
                                .build();
                    })
                    .toList();

            userAgreementRepository.saveAll(userAgreements);
        }

        // 5. 고정 지출 리스트 저장
        // 사용자가 입력한 고정 지출 항목들을 유저 정보랑 매핑해 저장
        if (request.getFixedExpenditures() != null && !request.getFixedExpenditures().isEmpty()) {
            List<FixedExpenditure> expenditures = request.getFixedExpenditures().stream()
                    .map(dto -> FixedExpenditure.builder()
                            .item(dto.getItem())
                            .amount(dto.getAmount())
                            .user(user)
                            .build())
                    .collect(Collectors.toList());

            fixedExpenditureRepository.saveAll(expenditures);
        }

        // 6. 회원가입 완료 후 Redis 내 인증 정보 삭제
        redisTemplate.delete(VERIFIED_PREFIX + request.getPhoneNumber());
    }
}
