package com.itcotato.naengjango.domain.snowball.controller;

import com.itcotato.naengjango.domain.member.dto.SnowballResponseDto;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.snowball.exception.code.SnowballSuccessCode;
import com.itcotato.naengjango.domain.snowball.service.SnowballService;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(
            summary = "눈덩이 요약 조회 by 임준서 (개발 완료)",
            description = """
                    눈덩이 요약 조회 API 입니다.
                    - 현재 보유한 눈덩이 개수와 총 적립된 눈덩이 개수를 반환합니다.
                    """)
    @GetMapping("/summary")
    public ApiResponse<SnowballResponseDto.Summary> getSummary(
            @AuthenticationPrincipal Member member
    ) {
        return ApiResponse.onSuccess(SnowballSuccessCode.SNOWBALL_SUMMARY_SUCCESS,
                snowballService.getSummary(member)
        );
    }

    /** 눈덩이 내역 조회 */
    @Operation(
            summary = "눈덩이 내역 조회 by 임준서 (개발 완료)",
            description = """
                    눈덩이 내역 조회 API 입니다.
                    - 페이징 처리가 된 눈덩이 적립 및 사용 내역을 반환합니다.
                    - 요청 시 페이지 번호와 페이지 크기를 쿼리 파라미터로 전달할 수 있습니다.
                    - 기본 페이지 크기는 20이며, 필요에 따라 조정할 수 있습니다.
                    """)
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
