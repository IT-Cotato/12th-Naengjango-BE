package com.itcotato.naengjango.domain.report.controller;

import com.itcotato.naengjango.domain.member.entity.Member;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "분석 리포트", description = "분석 리포트 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "하루 가용 예산 및 파산 시나리오 조회 by 주성아 (개발 완료)",
            description = """
                    로그인한 사용자의 지출 패턴을 분석하여 '가용 예산 추이'와 '파산 시나리오'를 제공합니다.
                    
                    ### 반환 데이터 :
                    - **todayAvailable**: 오늘 사용할 수 있는 가용 예산입니다.
                    - **diffFromYesterday**: 어제 계산된 가용 예산 대비 오늘의 증감액입니다.
                    - **dailyTrends**: 최근 8일(7일 전~오늘)동안의 일별 가용 예산 변화를 제공합니다.
                    - **bankruptcyPrediction**: 최근 8일간의 지출 데이터를 기반으로 이번 달 중 언제 예산이 소진될지 예측합니다.
                                                 단, 지출 내역이 없을 경우 해당 월의 말일에 파산한다고 가정합니다.
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
            @AuthenticationPrincipal Member member) {

        ReportResponseDTO.DailyBudgetReportDTO result = reportService.getDailyBudgetReport(member);
        return ApiResponse.onSuccess(ReportSuccessCode.REPORT_GET_SUCCESS, result);
    }

    @Operation(
            summary = "냉동 절약 효과 리포트 조회 by 주성아 (개발 완료)",
            description = """
        주간 또는 월간 단위로 냉동 기능을 통해 절약한 금액과 성공률 데이터를 조회합니다.
        모든 성공률은 원본 실수값으로 반환되니 적절히 가공해서 사용해주세요!
        
        ### 쿼리 파라미터:
        - **week**: 이번 주 성공 금액, 최근 4주간의 주차별 성공률 추이, 최근 4주 요일별 히트맵
        - **month**: 이번 달 성공 금액, 최근 4개월간의 월별 성공률 추이, 최근 4개월 요일별 히트맵
        
        ### 반환 데이터:
        - **totalSavedAmount**: 선택한 기간(이번 주/이번 달) 동안의 총 절약 금액 합계입니다.
        - **diffFromLastPeriod**: 지난 기간 대비 절약 금액의 증감분입니다.
        - **totalFailedAmount**: 선택한 기간 동안 냉동 실패로 인해 놓친 총 금액입니다.
        - **diffFailedFromLastPeriod**: 지난 기간 대비 실패 금액의 증감분입니다.
        - **successTrends**: 선그래프용 시계열 데이터입니다. ('n주 전' 또는 'n월')
        - **successRateByDay**: 요일별 성공률 히트맵 데이터입니다.
        - **bestSavingTime**: 요일과 시간대를 분석해 가장 높은 절약 성공률을 보인 때를 반환합니다.
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
                    responseCode = "REPORT400_1",
                    description = "잘못된 기간 설정. week나 month가 아님"
            )
    })
    @GetMapping("/savings-effect")
    public ApiResponse<ReportResponseDTO.SavingsEffectDTO> getSavingsEffect(
            @AuthenticationPrincipal Member member,
            @RequestParam(name = "period", defaultValue = "week") String period) {

        ReportResponseDTO.SavingsEffectDTO result = reportService.getSavingsEffect(member, period);
        return ApiResponse.onSuccess(ReportSuccessCode.REPORT_GET_SUCCESS, result);
    }
}
