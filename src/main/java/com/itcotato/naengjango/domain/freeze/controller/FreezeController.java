package com.itcotato.naengjango.domain.freeze.controller;

import com.itcotato.naengjango.domain.freeze.dto.FreezeRequestDto;
import com.itcotato.naengjango.domain.freeze.dto.FreezeResponseDto;
import com.itcotato.naengjango.domain.freeze.enums.FreezeStatus;
import com.itcotato.naengjango.domain.freeze.exception.code.FreezeSuccessCode;
import com.itcotato.naengjango.domain.freeze.service.FreezeService;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/freezes")
public class FreezeController {

    private final FreezeService freezeService;

    /**
     * 냉동 생성 (수동 등록)
     */
    @Operation(
            summary = "냉동 생성 by 임준서 (개발 완료)",
            description = """
                    냉동 생성 API 입니다.
                    - 요청 바디에 냉동할 아이템 정보(이름, 카테고리, 가격 등)를 포함하여 전송합니다.
                    - 성공 시 생성된 냉동 정보(아이디, 상태, 냉동 시작 시간 등)를 반환합니다.
                    """)
    @PostMapping
    public ApiResponse<FreezeResponseDto.Create> create(
            @AuthenticationPrincipal Member member,
            @RequestBody FreezeRequestDto.Create request
    ) {
        FreezeResponseDto.Create response = freezeService.create(member, request);
        return ApiResponse.onSuccess(FreezeSuccessCode.FREEZE_CREATE_SUCCESS, response);
    }

    /**
     * 냉동 중 목록 조회
     * sort=latest | price
     */
    @Operation(
            summary = "냉동 품목 조회 by 임준서 (개발 완료)",
            description = """
                    냉동 중인 품목들을 조회하는 API 입니다.
                    - `sort` 파라미터로 정렬 기준을 선택할 수 있습니다.
                        - `latest`: 최신 순 (기본값)
                        - `price`: 가격 순
                    - 성공 시 냉동 중인 품목들의 리스트를 반환합니다.
                    """)
    @GetMapping
    public ApiResponse<List<FreezeResponseDto.Item>> getFrozenItems(
            @AuthenticationPrincipal Member member,
            @RequestParam(defaultValue = "latest") String sort
    ) {
        List<FreezeResponseDto.Item> response = freezeService.getFrozenItems(member, sort);
        return ApiResponse.onSuccess(FreezeSuccessCode.FREEZE_LIST_SUCCESS, response);
    }

    /**
     * 냉동 기록 수정
     */
    @Operation(
            summary = "냉동 기록 수정 by 임준서 (개발 완료)",
            description = """
                    냉동 기록 수정 API 입니다.
                    - `freezeId`: 수정할 냉동 기록의 ID
                    - 요청 바디에 수정할 냉동 기록 정보(이름, 카테고리, 가격 등)를 포함하여 전송합니다.
                    - 성공 시 수정된 냉동 기록 정보를 반환합니다.
                    """)
    @PatchMapping("/{freezeId}")
    public ApiResponse<Void> update(
            @AuthenticationPrincipal Member member,
            @PathVariable Long freezeId,
            @RequestBody FreezeRequestDto.Update request
    ) {
        freezeService.update(member, freezeId, request);
        return ApiResponse.onSuccess(FreezeSuccessCode.FREEZE_UPDATE_SUCCESS, null);
    }

    /**
     * 계속 냉동 (24H 연장, 다중)
     */
    @Operation(
            summary = "냉동 연장 by 임준서 (개발 완료)",
            description = """
                    냉동 연장 API 입니다.
                    - 요청 바디에 연장할 냉동 기록 ID 리스트를 포함하여 전송합니다.
                    - 성공 시 연장된 냉동 기록들의 정보를 반환합니다.
                    """)
    @PostMapping("/extend")
    public ApiResponse<FreezeResponseDto.BulkAction> extend(
            @AuthenticationPrincipal Member member,
            @RequestBody FreezeRequestDto.Ids request
    ) {
        FreezeResponseDto.BulkAction response =  freezeService.extend(member, request.freezeIds());
        return ApiResponse.onSuccess(FreezeSuccessCode.FREEZE_EXTEND_SUCCESS, response);
    }

    /**
     * 냉동 실패 (다중)
     */
    @Operation(
            summary = "냉동 실패 by 임준서 (개발 완료)",
            description = """
                    냉동 실패 처리 API 입니다.
                    - 요청 바디에 실패로 처리할 냉동 기록 ID 리스트를 포함하여 전송합니다.
                    - 성공 시 실패로 처리된 냉동 기록들의 정보를 반환합니다
                    """)
    @PostMapping("/fail")
    public ApiResponse<FreezeResponseDto.BulkAction> fail(
            @AuthenticationPrincipal Member member,
            @RequestBody FreezeRequestDto.Ids request
    ) {
        FreezeResponseDto.BulkAction response =  freezeService.fail(member, request.freezeIds());
        return ApiResponse.onSuccess(FreezeSuccessCode.FREEZE_PURCHASE_SUCCESS, response);
    }

    /**
     * 냉동 성공 (다중)
     */
    @Operation(
            summary = "냉동 성공 by 임준서 (개발 완료)",
            description = """
                    냉동 성공 처리 API 입니다.
                    - 요청 바디에 성공으로 처리할 냉동 기록 ID 리스트를 포함하여 전송합니다.
                    - 성공 시 성공으로 처리된 냉동 기록들의 정보를 반환합니다
                    """)
    @PostMapping("/success")
    public ApiResponse<FreezeResponseDto.BulkAction> success(
            @AuthenticationPrincipal Member member,
            @RequestBody FreezeRequestDto.Ids request
    ) {
        FreezeResponseDto.BulkAction response =  freezeService.success(member, request.freezeIds());
        return ApiResponse.onSuccess(FreezeSuccessCode.FREEZE_SUCCESS_SUCCESS, response);
    }

    /**
     * 선택 항목 기준 예산 미리보기
     */
    @Operation(
            summary = "선택 항목 예산 조회 by 임준서 (개발 완료)",
            description = """
                    선택한 냉동 항목들의 예산 미리보기 API 입니다.
                    - 요청 바디에 예산을 미리보기할 냉동 기록 ID 리스트를 포함하여 전송합니다.
                    - 성공 시 선택한 냉동 항목들의 총 예산 금액을 반환합니다.
                    """)
    @PostMapping("/budget-preview")
    public ApiResponse<FreezeResponseDto.BudgetPreview> budgetPreview(
            @AuthenticationPrincipal Member member,
            @RequestBody FreezeRequestDto.Ids request
    ) {
        FreezeResponseDto.BudgetPreview response  = freezeService.budgetPreview(member, request.freezeIds());
        return ApiResponse.onSuccess(FreezeSuccessCode.FREEZE_BUDGET_PREVIEW_SUCCESS, response);
    }
}