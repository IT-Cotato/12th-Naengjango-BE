package com.itcotato.naengjango.domain.crawler.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CrawlResponse", description = "상품 링크 크롤링 결과")
public record CrawlResponse(
        @Schema(description = "상품명 (추출 실패 시 null)", example = "오뉴이 로테 체크 프릴 롱 스커트")
        String title,

        @Schema(description = "가격(원) (추출 실패 시 null)", example = "25170")
        Integer price,

        @Schema(description = "해결된 최종 상품 URL", example = "https://zigzag.kr/p/161129424")
        String productUrl,

        @Schema(description = "사이트 식별자", example = "ZIGZAG", allowableValues = {
                "ZIGZAG", "ABLY", "MUSINSA", "OLIVEYOUNG", "OHOUSE", "UNKNOWN"
        })
        String site
) {}
