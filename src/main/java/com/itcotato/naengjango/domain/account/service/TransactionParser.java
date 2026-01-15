package com.itcotato.naengjango.domain.account.service;

import com.itcotato.naengjango.domain.account.dto.TransactionResponseDTO;
import com.itcotato.naengjango.domain.account.exception.code.AccountErrorCode;
import com.itcotato.naengjango.domain.account.util.CategoryClassifier;
import com.itcotato.naengjango.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 문자 내역 파싱하는 서비스 코드
 */
@Service
@RequiredArgsConstructor
public class TransactionParser {
    private final CategoryClassifier categoryClassifier;

    public TransactionResponseDTO.ParseResponseDTO parseSmsText(String rawText) {
        // 1. 일반 텍스트가 아닌 결제 내역 문자인지 검사
        // 결제, 승인, 입금 등 가계부 관련 키워드가 하나도 없다면 일반 텍스트로 간주
        if (!(rawText.contains("승인") || rawText.contains("결제") ||
                rawText.contains("입금") || rawText.contains("출금") || rawText.contains("취소"))) {
            throw new GeneralException(AccountErrorCode.PARSE_INVALID_FORMAT);
        }

        try {
            // 2. 타입(수입/지출) 판단
            String type = rawText.contains("입금") || rawText.contains("환급") ? "수입" : "지출";

            // 3. 금액 추출
            Long amount = null;
            Matcher amountMatcher = Pattern.compile("([\\d,]+)원").matcher(rawText);
            if (amountMatcher.find()) {
                amount = Long.parseLong(amountMatcher.group(1).replace(",", ""));
            }

            // 4. 날짜 추출
            String date = null;
            Matcher dateMatcher = Pattern.compile("(\\d{2}/\\d{2})").matcher(rawText);
            if (dateMatcher.find()) {
                date = LocalDate.now().getYear() + "-" + dateMatcher.group(1).replace("/", "-");
            }

            // 5. 필수 정보(금액/날짜) 누락 검사
            if (amount == null || date == null) {
                throw new GeneralException(AccountErrorCode.PARSE_MISSING_INFO);
            }

            // 6. 업체명 및 카테고리
            String description = extractDescription(rawText, type);
            String category = categoryClassifier.classify(type, description);

            return TransactionResponseDTO.ParseResponseDTO.builder()
                    .type(type)
                    .amount(amount)
                    .description(description)
                    .memo(rawText)
                    .date(date)
                    .category(category)
                    .build();

        } catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException(AccountErrorCode.PARSE_FAILED);
        }
    }

    private String extractDescription(String rawText, String type) {
// 1. [수입] 소괄호를 제거하기 전, "송금" 패턴이 있는지 먼저 확인
        if ("수입".equals(type)) {
            // 괄호 삭제 전 괄호 내 중요 정보 있는지 확인 -> 중요정보 있다면 추출
            // (송금: 김철수 정산) 같은 패턴에서 "김철수 정산"만 추출
            Pattern senderPattern = Pattern.compile("\\(송금:\\s*(.*?)\\)");
            Matcher senderMatcher = senderPattern.matcher(rawText);
            if (senderMatcher.find()) {
                return senderMatcher.group(1).trim();
            }
            // (이자), (배당) 처럼 짧은 소괄호 키워드 확인
            Pattern keywordPattern = Pattern.compile("\\((이자|배당|환급|정산)\\)");
            Matcher keywordMatcher = keywordPattern.matcher(rawText);
            if (keywordMatcher.find()) return keywordMatcher.group(1);
        }

        // 2. 소괄호 정보 및 [Web발신] 제거
        String cleaned = rawText.replaceAll("\\(.*?\\)", "").replaceAll("\\[Web발신\\]", "").trim();

        // 3. 시간, 날짜 제거
        cleaned = cleaned.replaceAll("\\d{2}/\\d{2}", "");
        cleaned = cleaned.replaceAll("\\d{2}:\\d{2}", "");

        // 4. 불필요한 단어 제거
        cleaned = cleaned.replaceAll("[가-힣]{1}\\*[가-힣]{1}님|신한카드|우리카드|NH농협|우리|국민|승인|결제|일시불|입금|완료|확인", "").trim();

        // 5. 금액(~원) 바로 뒤 단어 추출
        if ("수입".equals(type)) {
            Pattern incomePattern = Pattern.compile("[\\d,]+원\\s+(\\S+)");
            Matcher matcher = incomePattern.matcher(rawText);
            if (matcher.find()) {
                String store = matcher.group(1);
                if (!store.contains("입금") && !store.contains("완료")) {
                    return store;
                }
            }
        }

        // 6. [지출] 금액(~원) 바로 앞 단어 추출 시도
        Pattern storePattern = Pattern.compile("(\\S+)\\s+[\\d,]+원");
        Matcher matcher = storePattern.matcher(rawText);
        if (matcher.find()) {
            String store = matcher.group(1);
            if (!store.contains("승인") && !store.contains("결제") && !store.contains("님") && !store.contains("입금")) {
                return store;
            }
        }

        // 7. 위 방법들이 실패할 경우, 남은 텍스트에서 첫 단어 선택
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        String[] parts = cleaned.split(" ");

        for (String part : parts) {
            if (part.length() >= 2 && !part.endsWith("님")) {
                return part;
            }
        }

        return "기타";
    }
}
