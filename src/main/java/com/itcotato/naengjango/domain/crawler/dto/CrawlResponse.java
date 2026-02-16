package com.itcotato.naengjango.domain.crawler.dto;

public record CrawlResponse(
        String title,
        Integer price,
        String productUrl,
        String site
) {}
