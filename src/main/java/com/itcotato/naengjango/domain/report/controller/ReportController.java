package com.itcotato.naengjango.domain.report.controller;

import com.itcotato.naengjango.domain.report.dto.ReportResponseDTO;
import com.itcotato.naengjango.domain.report.exception.code.ReportSuccessCode;
import com.itcotato.naengjango.domain.report.service.ReportService;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "분석 리포트", description = "분석 리포트 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "하루 가용 예산 및 파산 시나리오 조회 by 주성아 (개발 완료)",
            description = """
                    로그인한 사용자의 이번 달 예산과 지출 데이터를 분석해 하루 가용 예산의 변화와 파산 시나리오 리포트를 제공합니다.
                    1. 하루 가용 예산: 오늘부터 말일까지 균등하게 사용할 수 있는 금액을 계산합니다.
                    2. 가용 예산 추이: 최근 8일(7일 전~오늘)동안의 일별 가용 예산 변화를 제공합니다.
                    3. 파산 시나리오: 각 날짜별 소비 습관이 유지될 경우, 이번 달 중 언제 예산이 소진될지 예측합니다.
                                   단, 지출 내역이 없을 경우 해당 월의 마지막 날에 파산한다고 가정합니다.
            """
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "REPORT200_1",
                    description = "분석 리포트 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER404_1",
                    description = "사용자를 찾을 수 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "REPORT404_1",
                    description = "설정된 예산이 없음"
            )
    })
    @GetMapping("/daily-budget")
    public ApiResponse<ReportResponseDTO.DailyBudgetReportDTO> getDailyBudgetReport(
            @AuthenticationPrincipal Long memberId) {

        ReportResponseDTO.DailyBudgetReportDTO result = reportService.getDailyBudgetReport(memberId);
        return ApiResponse.onSuccess(ReportSuccessCode.REPORT_GET_SUCCESS, result);
    }
}
