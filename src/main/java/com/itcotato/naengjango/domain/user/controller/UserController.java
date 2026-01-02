package com.itcotato.naengjango.domain.user.controller;

import com.itcotato.naengjango.domain.user.dto.SmsRequestDTO;
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

@Tag(name = "사용자", description = "사용자 회원가입 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "ID 중복 확인 by 주성아",
            description = """
                회원가입 시 ID 중복 확인하는 API입니다.
                - `loginId`: 중복 여부를 확인할 아이디
                - 중복된 아이디가 존재하면 에러를 반환합니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "USER200_1",
                    description = "사용 가능한 아이디입니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "USER400_1",
                    description = "이미 존재하는 아이디입니다."
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
}
