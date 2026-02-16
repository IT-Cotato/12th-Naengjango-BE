package com.itcotato.naengjango.domain.crawler.support.parse;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class MetaParser {

    public String extractTitle(Document doc) {
        // 1) OG
        String og = meta(doc, "property", "og:title");
        if (notBlank(og)) return og;

        // 2) twitter
        String tw = meta(doc, "name", "twitter:title");
        if (notBlank(tw)) return tw;

        // 3) meta name=title
        String mt = meta(doc, "name", "title");
        if (notBlank(mt)) return mt;

        // 4) <title>
        String title = doc.title();
        if (notBlank(title)) return title.trim();

        return null;
    }

    private String meta(Document doc, String attrKey, String attrVal) {
        Element el = doc.selectFirst("meta[" + attrKey + "=" + attrVal + "]");
        if (el == null) return null;
        String content = el.attr("content");
        return notBlank(content) ? content.trim() : null;
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
