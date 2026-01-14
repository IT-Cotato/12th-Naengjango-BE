package com.itcotato.naengjango.domain.member.controller;

import com.itcotato.naengjango.domain.member.dto.SmsRequestDTO;
import com.itcotato.naengjango.domain.member.exception.code.SmsErrorCode;
import com.itcotato.naengjango.domain.member.exception.code.SmsSuccessCode;
import com.itcotato.naengjango.domain.member.service.SmsService;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "전화번호 인증", description = "SMS 인증 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sms")
public class Smscontroller {
    private final SmsService smsService;

    @Operation(summary = "SMS 인증번호 발송 by 주성아 (개발 완료)",
            description = """
                사용자 휴대폰으로 4자리 인증번호를 발송하는 API입니다.
                - `phoneNumber`: 인증번호를 받을 핸드폰 번호
                - 발송된 번호는 서버 메모리에 5분간 유지됩니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "SMS200_1",
                    description = "인증번호 발송 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "SMS500_1",
                    description = "문자 발송 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "SMS400_3",
                    description = "이미 가입된 전화번호"
            )
    })

    @PostMapping("/send")
    public ApiResponse<String> sendSms(@RequestBody SmsRequestDTO.SmsSendDTO request) {
        String result = smsService.sendVerificationSms(request.getPhoneNumber());

        // 입력한 번호가 이미 가입된 번호일 때
        if ("ALREADY_EXISTS".equals(result)) {
            return ApiResponse.onFailure(SmsErrorCode.SMS_ALREADY_EXISTS, "이미 가입된 휴대폰 번호입니다.");
        }

        // 인증번호를 성공적으로 발송했을 때
        return ApiResponse.onSuccess(SmsSuccessCode.SMS_SEND_SUCCESS, "인증번호가 발송되었습니다.");
    }

    @Operation(
            summary = "SMS 인증번호 검증 by 주성아(개발 완료)",
            description = """
                발송된 인증번호와 사용자가 입력한 번호가 일치하는지 확인합니다.
                - `phoneNumber`: 인증받은 핸드폰 번호
                - `verifyCode`: 수신한 4자리 인증번호
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "SMS200_2",
                    description = "인증 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "SMS400_1",
                    description = "인증번호 불일치"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "SMS400_2",
                    description = "인증시간 만료"
            ),
    })

    @PostMapping("/verify")
    public ApiResponse<String> verifySms(@RequestBody SmsRequestDTO.SmsVerifyDTO request) {
        String result = smsService.verifyCode(request.getPhoneNumber(), request.getVerifyCode());

        // 인증 성공했을 때
        if ("SUCCESS".equals(result)) {
            return ApiResponse.onSuccess(SmsSuccessCode.SMS_VERIFY_SUCCESS, "인증에 성공하였습니다.");
        }

        // 인증시간 만료되었을 때
        if ("EXPIRED".equals(result)) {
            return ApiResponse.onFailure(SmsErrorCode.SMS_VERIFY_EXPIRED, null);
        }

        // 인증번호 틀렸을 때
        return ApiResponse.onFailure(SmsErrorCode.SMS_BAD_REQUEST, null);
    }
}