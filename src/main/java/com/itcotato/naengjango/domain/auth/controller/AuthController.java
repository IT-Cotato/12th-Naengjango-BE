package com.itcotato.naengjango.domain.auth.controller;

import com.itcotato.naengjango.domain.auth.dto.*;
import com.itcotato.naengjango.domain.auth.exception.AuthException;
import com.itcotato.naengjango.domain.auth.exception.code.AuthErrorCode;
import com.itcotato.naengjango.domain.auth.exception.code.AuthSuccessCode;
import com.itcotato.naengjango.domain.auth.service.AuthService;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import com.itcotato.naengjango.global.redis.RefreshTokenRedisRepository;
import com.itcotato.naengjango.global.security.jwt.JwtClaims;
import com.itcotato.naengjango.global.security.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 / 로그인 API")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

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
    @PostMapping("/login")
    public ApiResponse<AuthResponseDto.TokenResponse> localLogin(
            @RequestBody @Valid AuthRequestDto.LoginRequest request
    ) {
        AuthResponseDto.TokenResponse response = authService.localLogin(request);
        return ApiResponse.onSuccess(AuthSuccessCode.LOGIN_SUCCESS, response);
    }

    /**
     * 토큰 재발급
     */
    @Operation(
            summary = "토큰 재발급 by 임준서 (개발 완료)",
            description = """
                            토큰 재발급 API 입니다.
                            - 요청 헤더에 refresh token을 포함하여 전송합니다.
                            - 성공 시 새로운 access token과 refresh token을 반환합니다.
                    """)
    @PostMapping("/refresh")
    public ApiResponse<AuthResponseDto.TokenResponse> refresh(
            @Valid @RequestBody AuthRequestDto.RefreshTokenRequest request
    ) {

        String refreshToken = request.refreshToken();

        // refresh token 검증
        JwtClaims claims = jwtProvider.validateAndExtractClaims(refreshToken);
        Long memberId = claims.memberId();

        // Redis에 저장된 토큰과 비교
        String savedToken = refreshTokenRedisRepository.findByMemberId(memberId)
                .orElseThrow(() -> new AuthException(AuthErrorCode.REISSUE_INVALID_REFRESH_TOKEN));

        if (!savedToken.equals(refreshToken)) {
            throw new AuthException(AuthErrorCode.REISSUE_INVALID_REFRESH_TOKEN);
        }

        // 새 토큰 발급
        AuthResponseDto.TokenResponse tokenResponse =
                authService.refresh(memberId);

        return ApiResponse.onSuccess(
                AuthSuccessCode.TOKEN_REISSUE_SUCCESS,
                tokenResponse
        );
    }

    /**
     * 로그아웃
     */
    @Operation(
            summary = "로그아웃 by 임준서 (개발 완료)",
            description = """
                            로그아웃 API 입니다.
                            - 요청 헤더에 access token을 포함하여 전송합니다.
                            - 성공 시 로그아웃 완료 메시지를 반환합니다.
                    """)
    @PostMapping("/logout")
    public ApiResponse<Void> logout(
            @AuthenticationPrincipal Long memberId
    ) {
        authService.logout(memberId);
        return ApiResponse.onSuccess(AuthSuccessCode.LOGOUT_SUCCESS,null);
    }

    @Operation(summary = """
         구글 로그인 리다이렉트 by 임준서 (개발 완료)""",
            description = """
                    구글 로그인은 아래 엔드포인트로 요청을 보내면 구글 OAuth2 인증 페이지로 리다이렉트됩니다.
                    - 사용자가 구글 계정으로 인증을 완료하면, 설정된 리다이렉트 URI로 다시 리다이렉트됩니다.
                    - 이후 백엔드에서 OAuth2 인증 정보를 처리하여 로그인 또는 회원가입을 진행합니다.
                    """)
    @GetMapping("/login/google")
    public void googleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @PostMapping("/find-loginId")
    public ApiResponse<AuthResponseDto.FindLoginIdResponse> findLoginId(
            @RequestBody AuthRequestDto.FindLoginIdRequest request
    ) {
        String masked = authService.findLoginId(
                request.name(),
                request.phoneNumber()
        );
        AuthResponseDto.FindLoginIdResponse response = new AuthResponseDto.FindLoginIdResponse(masked);
        return ApiResponse.onSuccess(AuthSuccessCode.FIND_LOGINID_SUCCESS, response);
    }

    @PostMapping("/find-password")
    public ApiResponse<Void> findPassword(
            @RequestBody AuthRequestDto.FindPasswordRequest request
    ) {
        authService.resetPassword(
                request.name(),
                request.loginId(),
                request.phoneNumber()
        );
        return ApiResponse.onSuccess(AuthSuccessCode.FIND_PASSWORD_SUCCESS, null);
    }
}
