package com.itcotato.naengjango.domain.crawler.support.resolve;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UrlResolver {

    private static final OkHttpClient NO_REDIRECT = new OkHttpClient.Builder()
            .followRedirects(false)
            .followSslRedirects(false)
            .build();

    private static final OkHttpClient FOLLOW_REDIRECT = new OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .build();

    // window.location / location.href / meta refresh 용
    private static final Pattern JS_REDIRECT = Pattern.compile(
            "(?i)(?:location\\.(?:href|replace)\\s*=\\s*|window\\.location\\s*=\\s*)['\"]([^'\"]+)['\"]"
    );

    public String resolveToWebUrl(String inputUrl) {
        String current = inputUrl;

        // 올리브영 snsBridge면 goodsNo로 변환해서 시작
        current = normalizeOliveyoung(current);

        for (int i = 0; i < 10; i++) {
            Request request = new Request.Builder()
                    .url(current)
                    .header("User-Agent", uaMobile())
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8")
                    .get()
                    .build();

            try (Response response = NO_REDIRECT.newCall(request).execute()) {
                int code = response.code();

                // 1) 3xx 리다이렉트면 Location 분석
                if (code >= 300 && code < 400) {
                    String loc = response.header("Location");
                    if (loc == null) break;

                    // fallback_desktop 같은 파라미터 우선
                    String fallback = extractQueryParam(loc, "fallback_desktop");
                    if (fallback != null && !fallback.isBlank()) {
                        return normalizeOliveyoung(urlDecode(fallback));
                    }

                    // 일반 Location 이동
                    HttpUrl base = HttpUrl.parse(current);
                    HttpUrl next = (base != null) ? base.resolve(loc) : HttpUrl.parse(loc);
                    if (next == null) break;

                    current = normalizeOliveyoung(next.toString());
                    continue;
                }

                // 2) 200인데도 JS/meta refresh로 튕기는 브릿지 페이지 처리
                if (code >= 200 && code < 300) {
                    String host = safeHost(current);

                    if (isBridgeHost(host)) {
                        String body = response.body() != null ? response.body().string() : "";
                        String redirected = extractRedirectFromHtml(body, current);
                        if (redirected != null) {
                            current = normalizeOliveyoung(redirected);
                            continue;
                        }

                        // 그래도 못 풀면 followRedirects=true로 한번 더
                        String finalUrl = resolveByFollowingRedirects(current);
                        return normalizeOliveyoung(finalUrl);
                    }

                    // 일반 페이지면 여기서 종료
                    return current;
                }

                return current;

            } catch (IOException e) {
                return current;
            }
        }

        // 마지막 fallback
        return normalizeOliveyoung(resolveByFollowingRedirects(current));
    }

    private String resolveByFollowingRedirects(String url) {
        Request req = new Request.Builder()
                .url(url)
                .header("User-Agent", uaMobile())
                .header("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8")
                .get()
                .build();

        try (Response res = FOLLOW_REDIRECT.newCall(req).execute()) {
            // OkHttp는 최종 요청 URL이 res.request().url()
            return res.request().url().toString();
        } catch (IOException e) {
            return url;
        }
    }

    private String extractRedirectFromHtml(String html, String baseUrl) {
        if (html == null || html.isBlank()) return null;

        // 1) meta refresh
        Document doc = Jsoup.parse(html, baseUrl);
        Element meta = doc.selectFirst("meta[http-equiv=refresh], meta[http-equiv=Refresh]");
        if (meta != null) {
            String content = meta.attr("content"); // e.g. "0; url=https://..."
            String url = extractRefreshUrl(content);
            if (url != null) return url;
        }

        // 2) JS redirect
        Matcher m = JS_REDIRECT.matcher(html);
        if (m.find()) {
            String target = m.group(1);
            HttpUrl base = HttpUrl.parse(baseUrl);
            HttpUrl resolved = (base != null) ? base.resolve(target) : HttpUrl.parse(target);
            return resolved != null ? resolved.toString() : target;
        }

        // 3) 흔한 파라미터 (airbridge/onelink에서 종종 있음)
        String fallback = extractQueryParam(html, "af_web_dp");
        if (fallback != null) return urlDecode(fallback);

        return null;
    }

    private String extractRefreshUrl(String content) {
        if (content == null) return null;
        // "0; url=..." 형태
        int idx = content.toLowerCase().indexOf("url=");
        if (idx < 0) return null;
        String u = content.substring(idx + 4).trim();
        return u.replaceAll("^['\"]|['\"]$", "");
    }

    private boolean isBridgeHost(String host) {
        if (host == null) return false;
        return host.contains("onelink.me")
                || host.contains("airbridge.io")
                || host.contains("applink.a-bly.com")
                || host.contains("ozip.me");
    }

    private String normalizeOliveyoung(String url) {
        HttpUrl h = HttpUrl.parse(url);
        if (h == null) return url;

        if (!h.host().contains("oliveyoung")) return url;

        // 어떤 형태로 들어오든 goodsNo만 뽑아서 PC 상세로 고정
        String goodsNo = h.queryParameter("goodsNo");

        // snsBridge.do?sndType=goods&sndVal=... 형태 지원
        if ((goodsNo == null || goodsNo.isBlank())
                && h.encodedPath().contains("/m/common/snsBridge.do")) {

            String sndType = h.queryParameter("sndType");
            String sndVal = h.queryParameter("sndVal");
            if ("goods".equalsIgnoreCase(sndType)) goodsNo = sndVal;
        }

        if (goodsNo != null && !goodsNo.isBlank()) {
            return "https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=" + goodsNo;
        }

        return url;
    }


    public String detectSite(String url) {
        String host = safeHost(url);
        if (host.contains("zigzag")) return "ZIGZAG";
        if (host.contains("a-bly") || host.contains("ably") || host.contains("airbridge.io")) return "ABLY";
        if (host.contains("musinsa") || host.contains("onelink.me")) return "MUSINSA";
        if (host.contains("oliveyoung")) return "OLIVEYOUNG";
        if (host.contains("ohou") || host.contains("ohouse") || host.contains("ozip")) return "OHOUSE";
        return "UNKNOWN";
    }

    private String extractQueryParam(String url, String key) {
        HttpUrl parsed = HttpUrl.parse(url);
        if (parsed != null) {
            return parsed.queryParameter(key);
        }
        int q = url.indexOf('?');
        if (q < 0) return null;
        String query = url.substring(q + 1);
        for (String part : query.split("&")) {
            int eq = part.indexOf('=');
            if (eq < 0) continue;
            String k = part.substring(0, eq);
            String v = part.substring(eq + 1);
            if (k.equals(key)) return v;
        }
        return null;
    }

    private String urlDecode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private String safeHost(String url) {
        HttpUrl h = HttpUrl.parse(url);
        return h != null ? h.host() : "";
    }

    private String uaMobile() {
        return "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Mobile Safari/537.36";
    }
}
