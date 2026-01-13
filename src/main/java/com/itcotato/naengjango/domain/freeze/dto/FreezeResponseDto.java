package com.itcotato.naengjango.domain.freeze.dto;

import com.itcotato.naengjango.domain.freeze.enums.FreezeStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class FreezeResponseDto {

    /**
     * 냉동 등록 응답
     */
    public record CreateResponse(
            Long freezeItemId,
            String itemName,
            int price,
            FreezeStatus status,
            LocalDateTime frozenAt,
            LocalDateTime deadline
    ) {}

    /**
     * 냉동 목록 응답
     */
    public record ListResponse(
            Long freezeItemId,
            String itemName,
            int price,
            FreezeStatus status,
            LocalDateTime deadline
    ) {}

    /**
     * 냉동 상세 응답
     */
    public record DetailResponse(
            Long freezeItemId,
            String appName,
            String itemName,
            int price,
            FreezeStatus status,
            LocalDateTime frozenAt,
            LocalDateTime deadline
    ) {}

    /**
     * 일괄 처리 결과 응답
     */
    public record BatchResultResponse(

            @Schema(example = "3")
            int processedCount,

            @Schema(
                    description = "처리된 냉동 항목 ID 목록",
                    example = "[1, 2, 3]"
            )
            List<Long> processedIds,

            @Schema(
                    description = "처리 결과 메시지",
                    example = "냉동 성공 처리 완료"
            )
            String message
    ) {
        public static BatchResultResponse from(
                List<Long> ids,
                String message
        ) {
            return new BatchResultResponse(
                    ids.size(),
                    ids,
                    message
            );
        }
    }
}
