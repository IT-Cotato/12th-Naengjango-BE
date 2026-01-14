package com.itcotato.naengjango.domain.auth.controller;

import com.itcotato.naengjango.domain.auth.dto.*;
import com.itcotato.naengjango.domain.auth.exception.code.AuthSuccessCode;
import com.itcotato.naengjango.domain.auth.service.AuthService;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 / 로그인 API")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 기본 로그인 (아이디 + 비밀번호)
     */
    @Operation(
            summary = "기본 로그인 by 임준서 (개발 완료)",
            description = """
                            기본 사용자 로그인 API 입니다.
                            - 요청 바디에 username과 password를 포함하여 전송합니다.
                            - 성공 시 access token과 refresh token을 반환합니다.
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN200_1",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN400_1",
                    description = "잘못된 요청 (로그인 타입 오류)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER404_1",
                    description = "회원 정보 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN401_1",
                    description = "비밀번호 불일치"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN401_2",
                    description = "리프레시 토큰 오류"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN500_1",
                    description = "서버 내부 오류"
            )
    })
    @PostMapping("/login")
    public ApiResponse<LoginResponseDto> loginLocal(
            @RequestBody @Valid LocalLoginRequestDto request
    ) {
        LoginResponseDto response = authService.loginLocal(request);
        return ApiResponse.onSuccess(AuthSuccessCode.LOGIN_SUCCESS, response);
    }

    /**
     * 소셜 로그인
     */
    @Operation(
            summary = "소셜 로그인 by 임준서 (개발 완료)",
            description = """
                            소셜 로그인 API 입니다.
                            - 소셜 타입(GOOGLE)에 따라 경로를 지정합니다.
                            - 요청 바디에 소셜 액세스 토큰을 포함하여 전송합니다.
                            - 기존 연동 계정일 경우 access token과 refresh token을 반환합니다.
                            - 최초 연동 계정일 경우 추가 회원가입 정보가 필요함을 알리는 응답을 반환합니다.
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN200_1",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN400_1",
                    description = "잘못된 요청 (로그인 타입 오류)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER404_1",
                    description = "회원 정보 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN401_1",
                    description = "비밀번호 불일치"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN401_2",
                    description = "리프레시 토큰 오류"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN500_1",
                    description = "서버 내부 오류"
            )
    })
    @PostMapping("/login/{socialType}")
    public ApiResponse<?> loginSocial(
            @PathVariable String socialType,
            @RequestBody @Valid SocialLoginRequestDto request
    ) {
        Object result = authService.loginSocial(socialType, request);
        return ApiResponse.onSuccess(AuthSuccessCode.LOGIN_SUCCESS, result);
    }

    /**
     * 로그아웃
     */
    @Operation(
            summary = "로그아웃 by 임준서 (개발 완료)",
            description = """
                            사용자 로그아웃 API 입니다.
                            - Authorization Header에 access token(JWT)을 포함해야 합니다.
                            - 성공 시 서버에서 토큰을 무효화 처리합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGOUT200_1",
                    description = "로그아웃 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGOUT401_1",
                    description = "인증되지 않은 사용자 (토큰 없음 또는 만료)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGOUT500_1",
                    description = "서버 내부 오류"
            )
    })
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @RequestBody @Valid LogoutRequestDto request
    ) {
        authService.logout(request);
        return ApiResponse.onSuccess(AuthSuccessCode.LOGOUT_SUCCESS, null);
    }

    /**
     * 토큰 재발급
     */
    @Operation(
            summary = "토큰 재발급 by 임준서 (개발 완료)",
            description = """
                            Access Token 재발급 API 입니다.
                            - 요청 바디에 유효한 Refresh Token을 포함하여 전송합니다.
                            - 성공 시 새로운 Access Token을 반환합니다.
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "TOKEN200_1",
                    description = "토큰 재발급 성공",
                    content = @Content(schema = @Schema(implementation = TokenReissueResponseDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "TOKEN401_1",
                    description = "유효하지 않은 리프레시 토큰"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "TOKEN500_1",
                    description = "서버 내부 오류"
            )
    })
    @PostMapping("/token/refresh")
    public ApiResponse<TokenReissueResponseDto> reissueToken(
            @RequestBody @Valid TokenReissueRequestDto request
    ) {
        TokenReissueResponseDto response = authService.reissueAccessToken(request);
        return ApiResponse.onSuccess(AuthSuccessCode.TOKEN_REISSUE_SUCCESS, response);
    }
}
