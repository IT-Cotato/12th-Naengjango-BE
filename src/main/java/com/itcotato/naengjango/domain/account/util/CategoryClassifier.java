package com.itcotato.naengjango.domain.account.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import java.util.List;

/**
 * 문자 내역 파싱 시 카테고리를 분류하기 위한 분류기
 */

@Component
public class CategoryClassifier {
    private static final Map<String, String> EXPENSE_KEYWORDS = new HashMap<>();
    private static final Map<String, String> INCOME_KEYWORDS = new HashMap<>();

    static {
        /**
         * 지출 카테고리 분류 (식비 의류 문화생활 생필품 미용 의료 교육 경조사 교통비 기타)
         */

        // 1. 식비
        List.of("GS25", "CU", "세븐일레븐", "이마트24",
                        "맥도날드", "롯데리아", "버거킹", "KFC", "서브웨이",
                        "스타벅스", "투썸", "이디야", "메가커피", "빽다방",
                        "배달의민족", "요기요", "쿠팡이츠",
                        "한식", "분식", "김밥", "국밥", "백반",
                        "치킨", "피자", "족발", "보쌈",
                        "식당", "카페", "푸드")
                .forEach(k -> EXPENSE_KEYWORDS.put(k, "식비"));

        // 2. 의류
        List.of("무신사", "에이블리", "지그재그", "29CM", "브랜디", "SHEIN", "W컨셉", "크림", "KREAM",
                        "유니클로", "자라", "H&M", "스파오", "탑텐",
                        "ABC마트", "슈펜",
                        "백화점", "아울렛",
                        "의류", "패션",
                        "옷", "복장",
                        "나이키", "아디다스", "뉴발란스")
                .forEach(k -> EXPENSE_KEYWORDS.put(k, "의류"));

        // 3. 문화생활
        List.of("CGV", "롯데시네마", "메가박스",
                        "교보문고", "영풍문고", "알라딘",
                        "예스24", "인터파크", "티켓링크", "멜론티켓",
                        "넷플릭스", "NETFLIX",
                        "티빙", "TVING",
                        "웨이브", "WAVVE",
                        "디즈니", "DISNEY",
                        "영화", "공연", "전시", "티켓")
                .forEach(k -> EXPENSE_KEYWORDS.put(k, "문화생활"));

        // 4. 생필품
        List.of("다이소", "올리브영", "OLIVEYOUNG", "오늘의집",
                        "쿠팡", "COUPANG",
                        "마켓컬리", "KURLY",
                        "이마트", "홈플러스", "롯데마트", "코스트코",
                        "마트", "슈퍼",
                        "생활용품",
                        "잡화", "AliExpress", "Temu",
                        "편의점")
                .forEach(k -> EXPENSE_KEYWORDS.put(k, "생필품"));

        // 5. 미용
        List.of("미용실", "헤어샵", "HAIR",
                        "이발", "바버샵",
                        "네일", "NAIL",
                        "왁싱",
                        "피부관리",
                        "에스테틱",
                        "미용",
                        "뷰티",
                        "살롱",
                        "컷트", "염색", "펌")
                .forEach(k -> EXPENSE_KEYWORDS.put(k, "미용"));

        // 6. 의료
        List.of("병원", "의원",
                        "내과", "외과", "정형외과", "이비인후과",
                        "피부과", "치과",
                        "한의원",
                        "약국",
                        "PHARMACY",
                        "의료",
                        "진료비",
                        "검진",
                        "건강")
                .forEach(k -> EXPENSE_KEYWORDS.put(k, "의료"));

        // 7. 교육
        List.of("학원",
                        "독서실",
                        "스터디카페",
                        "STUDY",
                        "인강",
                        "강의",
                        "수강료",
                        "교육",
                        "에듀",
                        "교재",
                        "서점",
                        "등록금")
                .forEach(k -> EXPENSE_KEYWORDS.put(k, "교육"));

        // 8. 경조사
        List.of("축의금",
                        "부조금",
                        "조의금",
                        "결혼",
                        "장례",
                        "선물",
                        "GIFT",
                        "상품권",
                        "기프티콘",
                        "카카오선물",
                        "꽃집",
                        "FLOWER",
                        "답례")
                .forEach(k -> EXPENSE_KEYWORDS.put(k, "경조사"));

        // 9. 교통비
        List.of("택시",
                        "카카오T",
                        "KAKAO T",
                        "버스",
                        "지하철",
                        "교통카드",
                        "티머니",
                        "캐시비",
                        "코레일",
                        "KTX",
                        "SRT",
                        "주유소",
                        "충전소",
                        "주차",
                        "주차장",
                        "따릉이")
                .forEach(k -> EXPENSE_KEYWORDS.put(k, "교통비"));

        // 10. 기타 (위 키워드에 없는 경우 자동 분류)

        /**
         * 수입 카테고리 분류(급여 용돈 정산 환급 이자/배당 기타)
         */

        // 1. 급여
        List.of(
                "급여", "월급", "상여", "상여금",
                "급여지급", "급여입금",
                "월급여", "급여이체",
                "인건비", "급여대장",
                "알바비", "아르바이트", "파트타임",
                "일급", "주급",
                "시급",
                "급여정산",
                "급여지급일",
                "보너스",
                "성과급"
        ).forEach(k -> INCOME_KEYWORDS.put(k, "급여"));

        // 2. 용돈
        List.of(
                "용돈",
                "용돈입금",
                "부모님",
                "엄마", "아빠",
                "생활비",
                "생활비지원",
                "지원금",
                "개인송금",
                "송금",
                "계좌이체",
                "이체받음",
                "이체수신",
                "카카오송금",
                "토스송금",
                "토스",
                "카카오페이",
                "페이송금",
                "친구송금"
        ).forEach(k -> INCOME_KEYWORDS.put(k, "용돈"));

        // 3. 정산
        List.of(
                "정산",
                "정산금",
                "정산입금",
                "정산이체",
                "더치페이",
                "N분의1",
                "회비정산",
                "회식정산",
                "공동결제",
                "공동지출",
                "비용정산",
                "분담금",
                "분할정산",
                "차액정산",
                "환급정산",
                "카카오정산",
                "토스정산",
                "모임정산"
        ).forEach(k -> INCOME_KEYWORDS.put(k, "정산"));

        // 4. 환급
        List.of(
                "환급",
                "환급금",
                "환불",
                "환불금",
                "차액환급",
                "결제취소",
                "취소환불",
                "캐시백",
                "포인트환급",
                "페이백",
                "적립금환급",
                "국세환급",
                "지방세환급",
                "연말정산",
                "보험환급",
                "의료비환급",
                "통신비환급",
                "카드환급"
        ).forEach(k -> INCOME_KEYWORDS.put(k, "환급"));

        // 5. 이자/배당
        List.of(
                "이자",
                "이자입금",
                "예금이자",
                "적금이자",
                "대출이자환급",
                "배당",
                "배당금",
                "배당입금",
                "주식배당",
                "ETF배당",
                "펀드수익",
                "투자수익",
                "운용수익",
                "수익금",
                "증권",
                "증권입금",
                "금융수익",
                "이자수익"
        ).forEach(k -> INCOME_KEYWORDS.put(k, "이자/배당"));

        // 6. 기타 (위 키워드에 없는 경우 자동 분류)
    }

    /**
     * 타입에 따라 적절한 카테고리를 분류함
     */
    public String classify(String type, String storeName) {
        if (storeName == null || storeName.isBlank()) return "기타";

        // 1. 수입인 경우 수입 키워드에서 찾기
        if ("수입".equals(type)) {
            return INCOME_KEYWORDS.entrySet().stream()
                    .filter(entry -> storeName.contains(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse("기타");
        }

        // 2. 지출인 경우 지출 키워드에서 찾기
        return EXPENSE_KEYWORDS.entrySet().stream()
                .filter(entry -> storeName.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("기타");
    }
}
