package com.itcotato.naengjango.domain.crawler.service;

import com.itcotato.naengjango.domain.crawler.dto.CrawlResponse;

public interface CrawlService {
    CrawlResponse crawl(String inputUrl);
}
