package com.itcotato.naengjango.domain.auth.controller;

import com.itcotato.naengjango.domain.auth.dto.LoginRequestDto;
import com.itcotato.naengjango.domain.auth.dto.LoginResponseDto;
import com.itcotato.naengjango.domain.auth.exception.code.AuthSuccessCode;
import com.itcotato.naengjango.domain.auth.service.AuthService;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증 / 로그인 API")
@RequiredArgsConstructor
public class AuthController {

    // private final AuthService authService;

    @Operation(
            summary = "로그인",
            description = """
                            사용자 로그인 API 입니다.
                            - 요청 바디에 username과 password를 포함하여 전송합니다.
                            - 성공 시 access token(JWT)을 반환합니다.
                    """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN200_1",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN400_1",
                    description = "잘못된 요청 (형식 오류)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN401_1",
                    description = "아이디 또는 비밀번호 불일치"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "LOGIN500_1",
                    description = "서버 내부 오류"
            )
    })
    @PostMapping("login")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        // AuthService를 통해 로그인 처리
        return ApiResponse.onSuccess(
                AuthSuccessCode.LOGIN_SUCCESS,
                new LoginResponseDto("mocked-jwt-token") // 실제로는 authService.login(...) 호출
        );
    }

    @Operation(
            summary = "로그아웃",
            description = """
                            사용자 로그아웃 API 입니다.
                            - Authorization Header에 access token(JWT)을 포함해야 합니다.
                            - 성공 시 서버에서 토큰을 무효화 처리합니다.
                            (현재는 mock 처리)
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
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        // 실제로는 authService.logout(token) 호출
        return ApiResponse.onSuccess(
                AuthSuccessCode.LOGOUT_SUCCESS,
                null
        );
    }
}
