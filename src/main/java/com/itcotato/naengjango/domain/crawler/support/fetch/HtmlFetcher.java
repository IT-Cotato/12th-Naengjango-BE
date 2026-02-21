package com.itcotato.naengjango.domain.crawler.support.fetch;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class HtmlFetcher {

    public Document fetch(String url) {
        try {
            Connection conn = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8")
                    .header("Referer", "https://www.google.com/")
                    .timeout((int)Duration.ofSeconds(12).toMillis())
                    .followRedirects(true)
                    .ignoreHttpErrors(true);

            Connection.Response res = conn.execute();
            String body = res.body();

            if (body == null || body.isBlank()) {
                throw new IllegalStateException("Empty HTML body (status=" + res.statusCode() + "): " + url);
            }

            return res.parse();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to fetch HTML: " + url, e);
        }
    }
}
