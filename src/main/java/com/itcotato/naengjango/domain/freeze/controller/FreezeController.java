package com.itcotato.naengjango.domain.freeze.controller;

import com.itcotato.naengjango.domain.freeze.dto.FreezeRequestDto;
import com.itcotato.naengjango.domain.freeze.dto.FreezeResponseDto;
import com.itcotato.naengjango.domain.freeze.enums.FreezeStatus;
import com.itcotato.naengjango.domain.freeze.exception.code.FreezeSuccessCode;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/freeze")
public class FreezeController {

    // private final FreezeService freezeService;

    /**
     * 냉동하기 등록
     */
    @Operation(
            summary = "냉동하기 등록 by 임준서 (개발 중)",
            description = """
                    소비를 냉동 상태로 등록합니다.
                    - 현재는 서비스 미구현 상태로 임시 응답을 반환합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "FREEZE201_1",
                    description = "냉동 등록 성공"
            )
    })
    @PostMapping
    public ApiResponse<FreezeResponseDto.CreateResponse> createFreeze(
            @RequestBody FreezeRequestDto.CreateRequest request
    ) {
        return ApiResponse.onSuccess(
                FreezeSuccessCode.FREEZE_CREATE_SUCCESS,
                new FreezeResponseDto.CreateResponse(
                        1L,
                        request.itemName(),
                        request.price(),
                        FreezeStatus.FROZEN,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(24)
                )
        );
    }

    /**
     * 냉동 목록 조회
     */
    @Operation(summary = "냉동 목록 조회 by 임준서 (개발 중)",
            description = """
                    사용자의 냉동 목록을 조회합니다.
                    - 현재는 서비스 미구현 상태로 임시 응답을 반환합니다.
                    """
    )
    @GetMapping
    public ApiResponse<List<FreezeResponseDto.ListResponse>> getFreezeList() {

        List<FreezeResponseDto.ListResponse> response = List.of(
                new FreezeResponseDto.ListResponse(
                        1L,
                        "에어팟 프로",
                        329000,
                        FreezeStatus.FROZEN,
                        LocalDateTime.now().plusHours(12)
                )
        );

        return ApiResponse.onSuccess(
                FreezeSuccessCode.FREEZE_LIST_SUCCESS,
                response
        );
    }

    /**
     * 냉동 상세 조회
     */
    @Operation(summary = "냉동 상세 조회 by 임준서 (개발 중)",
            description = """
                    특정 냉동 항목의 상세 정보를 조회합니다.
                    - 현재는 서비스 미구현 상태로 임시 응답을 반환합니다.
                    """
    )
    @GetMapping("/{freezeId}")
    public ApiResponse<FreezeResponseDto.DetailResponse> getFreezeDetail(
            @PathVariable Long freezeId
    ) {
        return ApiResponse.onSuccess(
                FreezeSuccessCode.FREEZE_DETAIL_SUCCESS,
                new FreezeResponseDto.DetailResponse(
                        freezeId,
                        "쿠팡",
                        "에어팟 프로",
                        329000,
                        FreezeStatus.FROZEN,
                        LocalDateTime.now().minusHours(1),
                        LocalDateTime.now().plusHours(23)
                )
        );
    }

    /**
     * 냉동 구매 확정
     */
    @Operation(summary = "냉동 항목 구매 확정 by 임준서 (개발 중)",
            description = """
                    특정 냉동 항목의 구매를 확정합니다.
                    - 현재는 서비스 미구현 상태로 임시 응답을 반환합니다.
                    """
    )
    @PostMapping("/{freezeId}/purchase")
    public ApiResponse<Void> purchase(
            @PathVariable Long freezeId
    ) {
        return ApiResponse.onSuccess(
                FreezeSuccessCode.FREEZE_PURCHASE_SUCCESS,
                null
        );
    }

    /**
     * 냉동 취소 (임시)
     */
    @Operation(summary = "냉동 항목 구매 취소 by 임준서 (개발 중)",
            description = """
                    특정 냉동 항목의 구매를 취소합니다.
                    - 현재는 서비스 미구현 상태로 임시 응답을 반환합니다.
                    """
    )
    @PostMapping("/{freezeId}/cancel")
    public ApiResponse<Void> cancel(
            @PathVariable Long freezeId
    ) {
        return ApiResponse.onSuccess(
                FreezeSuccessCode.FREEZE_CANCEL_SUCCESS,
                null
        );
    }

    /**
     * 냉동 삭제 (임시)
     */
    @Operation(summary = "냉동 항목 삭제 by 임준서 (개발 중)",
            description = """
                    특정 냉동 항목을 삭제합니다.
                    - 현재는 서비스 미구현 상태로 임시 응답을 반환합니다.
                    """
    )
    @DeleteMapping("/{freezeId}")
    public ApiResponse<Void> delete(
            @PathVariable Long freezeId
    ) {
        return ApiResponse.onSuccess(
                FreezeSuccessCode.FREEZE_DELETE_SUCCESS,
                null
        );
    }
}
