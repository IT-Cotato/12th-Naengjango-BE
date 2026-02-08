package com.itcotato.naengjango.domain.favoriteapp.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * 지원하는 앱 목록
 * DB에서는 String으로만 저장
 */
public enum SupportedApp {

    TWENTYNINECM("29cm", "29CM"),
    ABLY("ably", "에이블리"),
    BAEMIN("baemin", "배달의민족"),
    LIGHTNINGMARKET("lightningmarket", "번개장터"),
    TODAYHOUSE("todayhouse", "오늘의집"),
    YOGIYO("yogiyo", "요기요"),
    ALIEXPRESS("aliexpress", "알리익스프레스"),
    BRANDI("brandi", "브랜디"),
    COUPANG("coupang", "쿠팡"),
    COUPANGEATS("coupangeats", "쿠팡이츠"),
    CARROT("danggeunmarket", "당근마켓"),
    INTERPARKTICKET("interparkticket", "인터파크 티켓"),
    JOONGGONARA("joonggonara", "중고나라"),
    KAKAO("kakao", "카카오"),
    KREAM("kream", "크림"),
    MELONTICKET("melonticket", "멜론티켓"),
    MUSINSA("musinsa", "무신사"),
    NAVER("naver", "네이버"),
    OLIVEYOUNG("oliveyoung", "올리브영"),
    SHEIN("shein", "쉬인"),
    SSG("ssg", "SSG"),
    TEMU("temu", "테무"),
    TOSS("toss", "토스"),
    ZIGZAG("zigzag", "지그재그");

    private final String iconKey;        // 프론트 key
    private final String displayName;

    SupportedApp(String iconKey, String displayName) {
        this.iconKey = iconKey;
        this.displayName = displayName;
    }

    public String getIconKey() {
        return iconKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** appName(String)이 지원 앱인지 판별 */
    public static Optional<SupportedApp> from(String appName) {
        return Arrays.stream(values())
                .filter(app ->
                        app.iconKey.equalsIgnoreCase(appName)
                                || app.displayName.equals(appName))
                .findFirst();
    }
}
