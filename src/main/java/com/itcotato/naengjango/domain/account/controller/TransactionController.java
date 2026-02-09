package com.itcotato.naengjango.domain.account.controller;

import com.itcotato.naengjango.domain.account.dto.TransactionRequestDTO;
import com.itcotato.naengjango.domain.account.dto.TransactionResponseDTO;
import com.itcotato.naengjango.domain.account.exception.code.AccountSuccessCode;
import com.itcotato.naengjango.domain.account.service.TransactionParser;
import com.itcotato.naengjango.domain.account.service.TransactionService;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "가계부", description = "가계부 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class TransactionController {
    private final TransactionParser transactionParser;
    private final TransactionService transactionService;

    /**
     * 문자 내역 파싱
     */
    @Operation(
            summary = "문자 내역 파싱 by 주성아 (개발 완료)",
            description = """
                은행 및 카드사로부터 받은 결제/입금 문자 텍스트를 분석하여 가계부 데이터를 자동으로 추출합니다.
                
                ### Request Body
                - **rawText**: 분석할 원문 문자 내용 (예: "신한카드 승인 GS25 2,200원 02/21 14:30")
                
                ### Response Result
                - **type**: 지출/수입 구분
                - **amount**: 파싱된 금액
                - **description**: 결제처 정보
                - **date**: 결제 일시
                - **category**: 추천 카테고리
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "PARSE200_1",
                    description = "문자 내역 파싱 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "PARSE400_1",
                    description = "거래 내역 문자가 아닌 일반 텍스트임"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "PARSE400_3",
                    description = "텍스트에서 금액 또는 날짜 정보를 찾을 수 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "PARSE400_4",
                    description = "분석 실패"
            )
    })
    @PostMapping("/parser")
    public ApiResponse<TransactionResponseDTO.ParseResponseDTO> parseTransaction(
            @RequestBody TransactionRequestDTO.ParseRequestDTO request
    ) {
        String rawText = request.getRawText();
        TransactionResponseDTO.ParseResponseDTO parseResult = transactionParser.parseSmsText(rawText);
        return ApiResponse.onSuccess(AccountSuccessCode.PARSE_SUCCESS, parseResult);
    }


    /**
     * 가계부 내역 저장
     */
    @Operation(
            summary = "가계부 내역 저장 by 주성아 (개발 완료)",
            description = """
                가계부 내역을 DB에 저장합니다.
                """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "TRANSACTION200_1",
                    description = "가계부 내역 저장 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "TRANSACTION400_1",
                    description = "금액 유효성 오류 (0원 이하)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "TRANSACTION400_2",
                    description = "날짜 형식 오류 (yyyy-MM-dd 아님)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "TRANSACTION400_3",
                    description = "필수 항목 누락"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "MEMBER404_1",
                    description = "존재하지 않는 회원"),
    })
    @PostMapping("/transactions")
    public ApiResponse<Boolean> createTransaction(
            @AuthenticationPrincipal Member member,
            @RequestBody TransactionRequestDTO.CreateDTO request) {

        transactionService.saveTransaction(member, request);
        return ApiResponse.onSuccess(AccountSuccessCode.TRANSACTION_SAVE_SUCCESS, true);
    }

    /**
     * 날짜별 내역 조회
     */

    @Operation(
            summary = "날짜별 내역 조회 by 주성아(개발 완료)",
            description = """
            현재 로그인한 사용자의 특정 날짜에 해당하는 가계부 지출/수입 내역 리스트를 조회합니다.
            - 본인 확인: `@AuthenticationPrincipal`을 통해 본인의 데이터만 조회할 수 있도록 권한을 검증합니다.
            - 날짜 범위 조회: 입력받은 `date` 파라미터를 기준으로 당일 `00:00:00`부터 `23:59:59` 사이의 모든 내역을 가져옵니다.
            
            ### Params
                - **date**: 날짜 yy-MM-dd 형식 (예: 2026-02-21)
                
           ### Response Result
                - **type**: 지출/수입 구분
                - **amount**: 금액
                - **description**: 결제처 정보
                - **memo**: 메모
                - **date**: 날짜
                - **category**: 카테고리
            """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "COMMON200",
                    description = "가계부 내역 조회 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "TRANSACTION400_5",
                    description = "날짜 형식 오류(yyyy-MM-dd 형식이 아님)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "ACCOUNT403_1",
                    description = "조회 권한 없음"
            )
    })

    @GetMapping("/transactions")
    public ApiResponse<List<TransactionResponseDTO.TransactionListDTO>> getTransactions(
            @AuthenticationPrincipal Member member,
            @RequestParam(name = "date") String date) {

        List<TransactionResponseDTO.TransactionListDTO> result = transactionService.getTransactionsByDate(member, date);

        return ApiResponse.onSuccess(AccountSuccessCode.ACCOUNT_STATUS_SUCCESS, result);
    }

    /**
     * 가계부 내역 수정
     */
    @Operation(
            summary = "가계부 내역 수정 by 주성아 (개발 완료)",
            description = """
            기존에 저장된 가계부 내역의 금액 등을 수정합니다.
            - 본인의 내역만 수정 가능합니다.
            """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "TRANSACTION200_2",
                    description = "내역 수정 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "TRANSACTION404_1",
                    description = "내역 조회 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "ACCOUNT403_1",
                    description = "수정 권한 없음"
            )
    })
    @PatchMapping("/transactions/{transaction_id}")
    public ApiResponse<Boolean> updateTransaction(
            @AuthenticationPrincipal Member member,
            @PathVariable(name = "transaction_id") Long transactionId,
            @RequestBody TransactionRequestDTO.UpdateDTO request) {

        transactionService.updateTransaction(member, transactionId, request);
        return ApiResponse.onSuccess(AccountSuccessCode.TRANSACTION_UPDATE_SUCCESS, true);
    }

    /**
     * 가계부 내역 삭제
     */
    @Operation(
            summary = "가계부 내역 삭제 by 주성아 (개발 완료)",
            description = """
            저장된 가계부 내역을 삭제합니다.
            - 본인의 내역만 삭제 가능합니다.
            - 삭제된 데이터는 복구할 수 없습니다.
            """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "TRANSACTION200_3",
                    description = "내역 삭제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "TRANSACTION404_1",
                    description = "해당 내역을 찾을 수 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "ACCOUNT403_1",
                    description = "삭제 권한이 없음"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "ACCOUNT400_6",
                    description = "삭제 중 오류 발생"
            )
    })
    @DeleteMapping("/transactions/{transaction_id}")
    public ApiResponse<Boolean> deleteTransaction(
            @AuthenticationPrincipal Member member,
            @PathVariable(name = "transaction_id") Long transactionId) {

        transactionService.deleteTransaction(member, transactionId);
        return ApiResponse.onSuccess(AccountSuccessCode.TRANSACTION_DELETE_SUCCESS, true);
    }
}