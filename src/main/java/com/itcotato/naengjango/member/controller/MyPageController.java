package com.itcotato.naengjango.member.controller;

import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import com.itcotato.naengjango.global.apiPayload.code.GeneralSuccessCode;
import com.itcotato.naengjango.member.dto.MyPageDto;
import com.itcotato.naengjango.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/me")
public class MyPageController {

    private final MemberService memberService;

    @GetMapping
    public ApiResponse<MyPageDto.MeResponse> me(@RequestAttribute("userId") Long userId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.getMe(userId));
    }

    @GetMapping("/budget")
    public ApiResponse<MyPageDto.BudgetResponse> budget(@RequestAttribute("userId") Long userId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.getBudget(userId));
    }

    @PatchMapping("/budget")
    public ApiResponse<MyPageDto.BudgetResponse> updateBudget(
            @RequestAttribute("userId") Long userId,
            @RequestBody @Valid MyPageDto.UpdateBudgetRequest req
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.updateBudget(userId, req));
    }
}
