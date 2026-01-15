package com.itcotato.naengjango.domain.account.controller;

import com.itcotato.naengjango.domain.account.dto.TransactionRequestDTO;
import com.itcotato.naengjango.domain.account.dto.TransactionResponseDTO;
import com.itcotato.naengjango.domain.account.exception.code.AccountSuccessCode;
import com.itcotato.naengjango.domain.account.service.TransactionParser;
import com.itcotato.naengjango.domain.account.service.TransactionService;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import com.itcotato.naengjango.global.security.userdetails.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            summary = "문자 내역 파싱 by 주성아 (개발 중)",
            description = """
                결제 문자 텍스트를 파싱해 지출 정보를 자동으로 추출합니다.
                
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
            @AuthenticationPrincipal Long memberId,
            @RequestBody TransactionRequestDTO.CreateDTO request) {

        transactionService.saveTransaction(memberId, request);
        return ApiResponse.onSuccess(AccountSuccessCode.TRANSACTION_SAVE_SUCCESS, true);
    }
}
