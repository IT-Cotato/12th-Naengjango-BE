package com.itcotato.naengjango.domain.user.service;

import com.itcotato.naengjango.domain.user.exception.UserException;
import com.itcotato.naengjango.domain.user.exception.code.SmsErrorCode;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SmsService {
    private final DefaultMessageService messageService;
    private final String fromNumber;
    private final StringRedisTemplate redisTemplate; // Redis 연결용

    /** 인증번호 접두사 (Redis 키 관리용) */
    private static final String SMS_PREFIX = "sms:";

    public SmsService(
            @Value("${coolsms.api.key}") String apiKey,
            @Value("${coolsms.api.secret}") String apiSecret,
            @Value("${coolsms.from}") String fromNumber,
            StringRedisTemplate redisTemplate) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
        this.fromNumber = fromNumber;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 인증번호를 생성하고 발송하며, Redis에 5분간 저장합니다.
     */
    public void sendVerificationSms(String phoneNumber) {
        String verificationCode = String.format("%04d", new Random().nextInt(10000));

        Message message = new Message();
        message.setFrom(fromNumber);
        message.setTo(phoneNumber);
        message.setText("[냉잔고] 인증번호는 [" + verificationCode + "] 입니다.");

        try {
            this.messageService.sendOne(new SingleMessageSendingRequest(message));

            // Redis에 저장 (Key: sms:010..., Value: 인증번호, TTL: 5분)
            redisTemplate.opsForValue().set(
                    SMS_PREFIX + phoneNumber,
                    verificationCode,
                    5,
                    TimeUnit.MINUTES
            );
        } catch (Exception e) {
            throw new UserException(SmsErrorCode.SMS_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 입력받은 번호가 Redis에 저장된 번호와 일치하는지 확인
     */
    public boolean verifyCode(String phoneNumber, String inputCode) {
        // Redis에서 키(전화번호)로 값(인증번호) 조회
        String savedCode = redisTemplate.opsForValue().get(SMS_PREFIX + phoneNumber);

        // 데이터가 없거나(만료됨) 번호가 일치하지 않으면 false
        if (savedCode == null || !savedCode.equals(inputCode)) {
            return false;
        }

        // 인증 성공 시 Redis 데이터 삭제 (1회용 인증번호)
        redisTemplate.delete(SMS_PREFIX + phoneNumber);
        return true;
    }
}
