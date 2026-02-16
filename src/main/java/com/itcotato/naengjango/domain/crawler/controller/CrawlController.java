package com.itcotato.naengjango.domain.crawler.controller;

import com.itcotato.naengjango.domain.crawler.dto.CrawlRequest;
import com.itcotato.naengjango.domain.crawler.dto.CrawlResponse;
import com.itcotato.naengjango.domain.crawler.service.CrawlService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crawl")
public class CrawlController {

    private final CrawlService crawlService;

    @PostMapping
    public CrawlResponse crawl(@RequestBody CrawlRequest request) {
        return crawlService.crawl(request.url());
    }
}
