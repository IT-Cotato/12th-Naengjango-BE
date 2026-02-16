package com.itcotato.naengjango.domain.crawler.service;

import com.itcotato.naengjango.domain.crawler.dto.CrawlResponse;
import com.itcotato.naengjango.domain.crawler.support.extract.PriceExtractor;
import com.itcotato.naengjango.domain.crawler.support.fetch.HtmlFetcher;
import com.itcotato.naengjango.domain.crawler.support.parse.MetaParser;
import com.itcotato.naengjango.domain.crawler.support.resolve.UrlResolver;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import okhttp3.HttpUrl;

@Service
@RequiredArgsConstructor
public class CrawlServiceImpl implements CrawlService {

    private final UrlResolver urlResolver;
    private final HtmlFetcher htmlFetcher;
    private final MetaParser metaParser;
    private final PriceExtractor priceExtractor;

    @Override
    public CrawlResponse crawl(String inputUrl) {
        String finalUrl = urlResolver.resolveToWebUrl(inputUrl);
        String site = urlResolver.detectSite(finalUrl);

        try {
            Document doc = htmlFetcher.fetch(finalUrl);
            String title = metaParser.extractTitle(doc);

            // 올리브영 차단 페이지 감지 → goodsNo 추출해서 PC 상세로 재시도
            if ("OLIVEYOUNG".equals(site) && isOliveyoungBlockedTitle(title)) {
                String goodsNo = extractGoodsNo(finalUrl);
                if (goodsNo != null) {
                    String retryUrl = "https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=" + goodsNo;
                    Document retryDoc = htmlFetcher.fetch(retryUrl);
                    title = metaParser.extractTitle(retryDoc);
                    Integer price = priceExtractor.extractPrice(retryDoc);
                    return new CrawlResponse(title, price, retryUrl, site);
                }
            }

            Integer price = priceExtractor.extractPrice(doc);
            return new CrawlResponse(title, price, finalUrl, site);

        } catch (Exception e) {
            return new CrawlResponse(null, null, finalUrl, site);
        }
    }

    private boolean isOliveyoungBlockedTitle(String title) {
        if (title == null) return false;
        String t = title.trim();
        return t.contains("잠시만 기다려") || t.equalsIgnoreCase("잠시만 기다려 주세요 - 올리브영");
    }

    private String extractGoodsNo(String url) {
        HttpUrl h = HttpUrl.parse(url);
        if (h == null) return null;
        String goodsNo = h.queryParameter("goodsNo");
        if (goodsNo != null && !goodsNo.isBlank()) return goodsNo;

        // 혹시 url에 sndVal로만 있을 때
        String sndVal = h.queryParameter("sndVal");
        if (sndVal != null && !sndVal.isBlank()) return sndVal;

        return null;
    }


}
