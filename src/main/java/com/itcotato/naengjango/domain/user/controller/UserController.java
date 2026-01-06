package com.itcotato.naengjango.domain.user.controller;

import com.itcotato.naengjango.domain.user.dto.SmsRequestDTO;
import com.itcotato.naengjango.domain.user.dto.UserRequestDTO;
import com.itcotato.naengjango.domain.user.exception.code.SmsErrorCode;
import com.itcotato.naengjango.domain.user.exception.code.SmsSuccessCode;
import com.itcotato.naengjango.domain.user.exception.code.UserErrorCode;
import com.itcotato.naengjango.domain.user.exception.code.UserSuccessCode;
import com.itcotato.naengjango.domain.user.service.UserService;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자 회원가입", description = "사용자 회원가입 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "ID 중복 확인 by 주성아 (개발 완료)",
            description = """
                회원가입 시 ID 중복 확인하는 API입니다.
                - `loginId`: 중복 여부를 확인할 아이디
                - 중복된 아이디가 존재하면 에러를 반환합니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "USER200_1",
                    description = "사용 가능한 아이디"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "USER400_1",
                    description = "이미 존재하는 아이디"
            )
    })

    @PostMapping("/check-id")
    public ApiResponse<Boolean> checkLoginId(@RequestParam String loginId) {
        boolean isDuplicate = userService.isLoginIdDuplicate(loginId);

        if (isDuplicate) {
            // 이미 존재하는 ID인 경우
            return ApiResponse.onFailure(UserErrorCode.USER_ID_ALREADY_EXISTS, true);
        }

        // 사용 가능한 ID인 경우 (중복이 아닌 경우)
        return ApiResponse.onSuccess(UserSuccessCode.USER_ID_AVAILABLE, false);
    }


    @Operation(summary = "최종 회원가입 API by 주성아",
            description = """
                회원가입 시 유저 정보를 저장하는 API입니다.
                - 이름, 전화번호, 아이디, 비밀번호, 동의한 약관 ID 리스트, 한 달 예산, 고정지출 리스트 저장합니다.
                - 인증 유효시간인 15분이 만료되면 에러를 반환합니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "USER200_2",
                    description = "회원가입 완료"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "SMS400_2",
                    description = "인증 시간 만료"
            )
    })

    @PostMapping("/signup")
    public ApiResponse<String> signup(@RequestBody UserRequestDTO.SignupDTO request) {
        userService.signup(request);
        return ApiResponse.onSuccess(UserSuccessCode.USER_SIGNUP_SUCCESS, "회원가입이 완료되었습니다.");
    }
}
