package com.itcotato.naengjango.domain.crawler.support.extract;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PriceExtractor {

    private static final ObjectMapper OM = new ObjectMapper();

    // "129,000원" 같은 텍스트
    private static final Pattern KRW_TEXT = Pattern.compile("(\\d{1,3}(?:,\\d{3})+|\\d{1,9})\\s*원");

    // JSON/스크립트에 흔히 나오는 키워드들(필요시 계속 추가)
    private static final Pattern PRICE_KEYS = Pattern.compile(
            "(?i)(salePrice|sale_price|discountPrice|discount_price|finalPrice|final_price|price|goodsPrc|goodsPrice|originPrice|originalPrice)\"?\\s*[:=]\\s*\"?(\\d{3,9})\"?"
    );

    public Integer extractPrice(Document doc) {
        // 1) meta 기반
        Integer metaPrice = parseIntSafe(
                firstNonBlank(
                        meta(doc, "property", "product:price:amount"),
                        meta(doc, "property", "og:price:amount"),
                        meta(doc, "itemprop", "price"),
                        meta(doc, "name", "twitter:data1") // 일부 사이트는 twitter card에 가격 넣기도 함
                )
        );
        if (metaPrice != null) return metaPrice;

        // 2) JSON-LD (offers.price)
        Integer jsonLdPrice = extractFromJsonLd(doc);
        if (jsonLdPrice != null) return jsonLdPrice;

        // 3) 스크립트 키워드 기반 정규식
        String html = doc.html();
        Integer keyPrice = extractByKeyRegex(html);
        if (keyPrice != null) return keyPrice;

        // 4) "원" 텍스트 기반(마지막 fallback)
        Integer krw = extractByKrwText(doc.text());
        return krw;
    }

    private Integer extractFromJsonLd(Document doc) {
        for (Element el : doc.select("script[type=application/ld+json]")) {
            String json = el.data();
            if (json == null || json.isBlank()) continue;

            try {
                JsonNode root = OM.readTree(json);

                // JSON-LD가 배열인 경우도 있음
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        Integer p = readOffersPrice(node);
                        if (p != null) return p;
                    }
                } else {
                    Integer p = readOffersPrice(root);
                    if (p != null) return p;
                }
            } catch (Exception ignore) {
                // 파싱 실패는 넘어감
            }
        }
        return null;
    }

    private Integer readOffersPrice(JsonNode node) {
        if (node == null || node.isNull()) return null;

        // offers: { price: "129000" } or offers: [{price: ...}]
        JsonNode offers = node.get("offers");
        if (offers == null) return null;

        if (offers.isArray()) {
            for (JsonNode o : offers) {
                Integer p = parseIntSafe(text(o.get("price")));
                if (p != null) return p;
            }
        } else {
            Integer p = parseIntSafe(text(offers.get("price")));
            if (p != null) return p;
        }

        return null;
    }

    private Integer extractByKeyRegex(String html) {
        Matcher m = PRICE_KEYS.matcher(html);
        while (m.find()) {
            String num = m.group(2);
            Integer p = parseIntSafe(num);
            if (p != null) return p;
        }
        return null;
    }

    private Integer extractByKrwText(String text) {
        Matcher m = KRW_TEXT.matcher(text);

        Integer best = null;
        while (m.find()) {
            Integer v = parseIntSafe(m.group(1));
            if (v == null) continue;

            // 할인/정가가 같이 있으면 보통 더 작은 값이 판매가
            if (best == null || v < best) best = v;
        }
        return best;
    }


    private String meta(Document doc, String attrKey, String attrVal) {
        Element el = doc.selectFirst("meta[" + attrKey + "=" + attrVal + "]");
        if (el == null) return null;
        String content = el.attr("content");
        return (content != null && !content.isBlank()) ? content.trim() : null;
    }

    private String firstNonBlank(String... arr) {
        for (String s : arr) {
            if (s != null && !s.isBlank()) return s;
        }
        return null;
    }

    private String text(JsonNode node) {
        if (node == null || node.isNull()) return null;
        if (node.isNumber()) return node.asText();
        if (node.isTextual()) return node.asText();
        return node.toString();
    }

    private Integer parseIntSafe(String s) {
        if (s == null) return null;
        String onlyNum = s.replaceAll("[^0-9]", "");
        if (onlyNum.isBlank()) return null;
        try {
            return Integer.parseInt(onlyNum);
        } catch (Exception e) {
            return null;
        }
    }
}
