package com.itcotato.naengjango.domain.member.controller;

import com.itcotato.naengjango.domain.member.dto.AgreementRequestDto;
import com.itcotato.naengjango.domain.member.dto.AgreementResponseDto;
import com.itcotato.naengjango.domain.member.dto.MemberRequestDTO;
import com.itcotato.naengjango.domain.member.dto.SmsRequestDTO;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.exception.code.MemberErrorCode;
import com.itcotato.naengjango.domain.member.exception.code.MemberSuccessCode;
import com.itcotato.naengjango.domain.member.service.MemberService;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 회원가입", description = "사용자 회원가입 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "ID 중복 확인 by 주성아 (개발 완료)",
            description = """
                회원가입 시 ID 중복 확인하는 API입니다.
                - `loginId`: 중복 여부를 확인할 아이디
                - 중복된 아이디가 존재하면 에러를 반환합니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER200_1",
                    description = "사용 가능한 아이디"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER400_1",
                    description = "이미 존재하는 아이디"
            )
    })

    @PostMapping("/check-id")
    public ApiResponse<Boolean> checkLoginId(@RequestParam String loginId) {
        boolean isDuplicate = memberService.isLoginIdDuplicate(loginId);

        if (isDuplicate) {
            // 이미 존재하는 ID인 경우
            return ApiResponse.onFailure(MemberErrorCode.MEMBER_ID_ALREADY_EXISTS, true);
        }

        // 사용 가능한 ID인 경우 (중복이 아닌 경우)
        return ApiResponse.onSuccess(MemberSuccessCode.MEMBER_ID_AVAILABLE, false);
    }


    @Operation(summary = "일반 회원가입 API by 주성아 (개발 완료)",
            description = """
                일반 회원가입 시 유저 정보를 저장하는 API입니다.
                - 이름, 전화번호, 아이디, 비밀번호, 동의한 약관 ID 리스트, 한 달 예산, 고정지출 리스트 저장합니다.
                - 인증 유효시간인 15분이 만료되면 에러를 반환합니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER200_2",
                    description = "회원가입 완료"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "SMS400_2",
                    description = "인증 시간 만료"
            )
    })

    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody MemberRequestDTO.SignupDTO request) {
        memberService.signup(request);
        return ApiResponse.onSuccess(MemberSuccessCode.MEMBER_SIGNUP_SUCCESS, "회원가입이 완료되었습니다.");
    }


    @Operation(summary = "구글 회원가입 API by 주성아 (개발 완료)",
            description = """
                구글 회원가입 시 유저 정보를 저장하는 API입니다.
                - 이름, 전화번호, 소셜ID, 동의한 약관 ID 리스트, 한 달 예산, 고정지출 리스트를 저장합니다.
                - 인증 정보가 유효하지 않거나 만료된 경우 에러를 반환합니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER200_2",
                    description = "소셜 회원가입 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER400_1",
                    description = "이미 가입된 소셜 계정"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "SMS400_2",
                    description = "인증 시간 만료 또는 유효하지 않은 요청"
            )
    })
    @PostMapping("/social-signup")
    public ApiResponse<String> socialSignup(@RequestBody MemberRequestDTO.SocialSignupDTO request) {
        memberService.signupSocial(request);

        return ApiResponse.onSuccess(MemberSuccessCode.MEMBER_SIGNUP_SUCCESS, "소셜 회원가입이 완료되었습니다.");
    }


    @Operation(
            summary = "전화번호 저장 API (OAuth2 사용자용)",
            description = """
                이미 로그인된 소셜 사용자가 전화번호를 저장하는 API입니다.
                - JWT 인증 필요
                - SMS 인증이 완료된 번호만 저장 가능
                """
    )
    @PatchMapping("/me/phone")
    public ApiResponse<String> updatePhoneNumber(
            @AuthenticationPrincipal Member member,
            @RequestBody SmsRequestDTO.SmsVerifyDTO request
    ) {
        memberService.updatePhoneNumber(
                member,
                request.getPhoneNumber(),
                request.getVerifyCode()
        );

        return ApiResponse.onSuccess(
                MemberSuccessCode.MEMBER_UPDATE_SUCCESS,
                "전화번호가 저장되었습니다."
        );
    }


    /**
     * 약관 동의 API
     */
    @Operation(
            summary = "약관 동의 API",
            description = """
                사용자가 약관에 동의하는 API입니다.
                - JWT 인증 필요
                - 요청 바디에 동의할 약관 ID 리스트를 포함하여 전송합니다.
                - 성공 시 동의한 약관 리스트를 반환합니다.
                """
    )
    @PostMapping("/agreements")
    public ApiResponse<Void> agreeAgreements(
            @AuthenticationPrincipal Member member,
            @RequestBody AgreementRequestDto.AgreeRequest request
    ) {

        memberService.agreeAgreements(member, request);

        return ApiResponse.onSuccess(MemberSuccessCode.MEMBER_UPDATE_SUCCESS, null);
    }
}
