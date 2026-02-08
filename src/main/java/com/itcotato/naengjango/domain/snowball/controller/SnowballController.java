package com.itcotato.naengjango.domain.snowball.controller;

import com.itcotato.naengjango.domain.member.dto.SnowballResponseDto;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.snowball.exception.code.SnowballSuccessCode;
import com.itcotato.naengjango.domain.snowball.service.SnowballService;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/snowballs")
@RequiredArgsConstructor
public class SnowballController {

    private final SnowballService snowballService;

    /** 눈덩이 요약 조회 */
    @GetMapping("/summary")
    public ApiResponse<SnowballResponseDto.Summary> getSummary(
            @AuthenticationPrincipal Member member
    ) {
        return ApiResponse.onSuccess(SnowballSuccessCode.SNOWBALL_SUMMARY_SUCCESS,
                snowballService.getSummary(member)
        );
    }

    /** 눈덩이 내역 조회 */
    @GetMapping("/history")
    public ApiResponse<Page<SnowballResponseDto.History>> getHistory(
            @AuthenticationPrincipal Member member,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ApiResponse.onSuccess(SnowballSuccessCode.SNOWBALL_HISTORY_SUCCESS,
                snowballService.getHistory(member, pageable)
        );
    }
}
