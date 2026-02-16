package com.itcotato.naengjango.domain.crawler.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CrawlRequest", description = "상품 링크 크롤링 요청")
public record CrawlRequest(
        @Schema(
                description = "상품 링크 (단축/딥링크/웹 링크 모두 허용)",
                example = "https://s.zigzag.kr/EMOffXPf65",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String url
) {}
