package com.itcotato.naengjango.member.controller;

import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import com.itcotato.naengjango.global.apiPayload.code.GeneralSuccessCode;
import com.itcotato.naengjango.member.dto.MyPageDto;
import com.itcotato.naengjango.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MemberService memberService;

    @Operation(
            summary = "마이페이지 내 정보 조회 API",
            description = "인증된 사용자의 내 정보(이름, loginId, 전화번호, 예산, 소셜타입, role)를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/me")
    public ApiResponse<MyPageDto.MeResponse> me(@RequestAttribute(value = "memberId", required = false) Long memberId) {
        if (memberId == null) throw new IllegalArgumentException("Missing request attribute: memberId");
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.getMe(memberId));
    }

    @Operation(
            summary = "마이페이지 예산 조회 API",
            description = "인증된 사용자의 예산(budget)을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/budget")
    public ApiResponse<MyPageDto.BudgetResponse> budget(@RequestAttribute("memberId") Long memberId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.getBudget(memberId));
    }

    @Operation(
            summary = "마이페이지 예산 수정 API",
            description = "인증된 사용자의 예산(budget)을 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/budget")
    public ApiResponse<MyPageDto.BudgetResponse> updateBudget(
            @RequestAttribute("memberId") Long memberId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "수정할 예산 정보",
                    content = @Content(schema = @Schema(implementation = MyPageDto.UpdateBudgetRequest.class))
            )
            @Valid @RequestBody MyPageDto.UpdateBudgetRequest request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.updateBudget(memberId, request));
    }
}
