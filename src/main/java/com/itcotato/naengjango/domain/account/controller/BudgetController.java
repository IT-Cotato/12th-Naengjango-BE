package com.itcotato.naengjango.domain.account.controller;

import com.itcotato.naengjango.domain.account.dto.BudgetResponseDTO;
import com.itcotato.naengjango.domain.account.exception.code.AccountSuccessCode;
import com.itcotato.naengjango.domain.account.service.BudgetService;
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

/**
 * 남은 예산 정보(오늘 예산/이번달 예산) 조회 관련 컨트롤러
 */

@Tag(name = "예산 조회", description = "남은 예산 정보(오늘 예산/이번달 예산) 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class BudgetController {
    private final BudgetService budgetService;

    @Operation(summary = "남은 예산 정보 조회 by 주성아 (개발 완료)",
            description = """
                현재 로그인한 사용자의 오늘 및 이번 달 남은 예산 정보를 조회합니다.
                - 오늘의 예산 = (한달 전체 예산 - 어제까지의 총 지출액) / (이번 달 남은 일수)
                - 사용자가 어제 지출을 아꼈다면 오늘 사용할 수 있는 예산이 자동으로 증액됩니다.
                - 계산된 예산보다 지출이 클 경우 마이너스(-)가 아닌 0원으로 반환합니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "ACCOUNT200_1",
                    description = "남은 예산 정보 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "ACCOUNT403_1",
                    description = "예산 정보 조회 접근 권한 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER404_1",
                    description = "해당 사용자를 찾을 수 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "ACCOUNT500_1",
                    description = "서버 에러"
            )
    })

    @GetMapping("/status")
    public ApiResponse<BudgetResponseDTO.BudgetStatusDTO> getBudgetStatus(
            @AuthenticationPrincipal Long memberId,
            @RequestParam int year,
            @RequestParam int month,
            @RequestParam int day) {

        BudgetResponseDTO.BudgetStatusDTO result = budgetService.getBudgetStatus(memberId, year, month, day);
        return ApiResponse.onSuccess(AccountSuccessCode.ACCOUNT_STATUS_SUCCESS, result);
    }
}
