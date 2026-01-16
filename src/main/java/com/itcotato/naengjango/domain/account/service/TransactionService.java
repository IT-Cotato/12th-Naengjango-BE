package com.itcotato.naengjango.domain.account.service;

import com.itcotato.naengjango.domain.account.dto.TransactionRequestDTO;
import com.itcotato.naengjango.domain.account.dto.TransactionResponseDTO;
import com.itcotato.naengjango.domain.account.entity.Transaction;
import com.itcotato.naengjango.domain.account.enums.PaymentMethod;
import com.itcotato.naengjango.domain.account.enums.TransactionType;
import com.itcotato.naengjango.domain.account.exception.code.AccountErrorCode;
import com.itcotato.naengjango.domain.account.repository.TransactionRepository;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.member.exception.code.MemberErrorCode;
import com.itcotato.naengjango.domain.member.repository.MemberRepository;
import com.itcotato.naengjango.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final MemberRepository memberRepository;

    /**
     * 가계부 내역 저장
     */

    public void saveTransaction(Long memberId, TransactionRequestDTO.CreateDTO request) {
        // 1. 회원인지 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 2. 유효성 검증
        validateRequest(request);

        try {
            // 3. DTO를 엔티티로 변환하여 저장
            Transaction transaction = Transaction.builder()
                    .member(member)
                    .type(request.getType().equals("수입") ? TransactionType.INCOME : TransactionType.EXPENSE)
                    .amount(request.getAmount())
                    .description(request.getDescription())
                    .memo(request.getMemo())
                    // "2026-02-21" -> "2026-02-21T00:00:00" 변환
                    .date(LocalDateTime.parse(request.getDate() + "T00:00:00"))
                    .payment(PaymentMethod.CARD)
                    .category(request.getCategory())
                    .build();

            transactionRepository.save(transaction);

        } catch (DateTimeParseException e) {
            // 날짜 형식이 yyyy-MM-dd가 아닐 경우 예외 발생
            throw new GeneralException(AccountErrorCode.INVALID_TRANSACTION_DATE);
        } catch (Exception e) {
            // 그 외 알 수 없는 저장 오류
            throw new GeneralException(AccountErrorCode.TRANSACTION_SAVE_FAILED);
        }
    }

    private void validateRequest(TransactionRequestDTO.CreateDTO request) {
        // 금액이 null이거나 0원 이하인 경우 예외처리
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new GeneralException(AccountErrorCode.INVALID_TRANSACTION_AMOUNT);
        }

        // 내역이나 카테고리가 비어있는 경우 예외처리
        if (request.getDescription() == null || request.getDescription().isBlank() ||
                request.getCategory() == null || request.getCategory().isBlank()) {
            throw new GeneralException(AccountErrorCode.MISSING_TRANSACTION_REQUIRED);
        }
    }

    /**
     * 날짜별 가계부 내역 조회
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO.TransactionListDTO> getTransactionsByDate(Long memberId, String date) {
        // 1. 조회 권한이 있는지 확인
        if (memberId == null) {
            throw new GeneralException(AccountErrorCode.ACCOUNT_FORBIDDEN);
        }

        try {
            // 2. 날짜 파싱 및 범위 설정
            LocalDateTime startOfDay = LocalDateTime.parse(date + "T00:00:00");
            LocalDateTime endOfDay = LocalDateTime.parse(date + "T23:59:59");

            // 3. 로그인된 memberId의 데이터 조회
            List<Transaction> transactions = transactionRepository.findAllByMemberIdAndDateBetween(
                    memberId, startOfDay, endOfDay);

            // 4. DTO 변환
            return transactions.stream()
                    .map(t -> TransactionResponseDTO.TransactionListDTO.builder()
                            .type(t.getType().name().equals("INCOME") ? "수입" : "지출")
                            .amount(t.getAmount())
                            .description(t.getDescription())
                            .memo(t.getMemo())
                            .date(t.getDate().toLocalDate().toString())
                            .category(t.getCategory())
                            .build())
                    .collect(Collectors.toList());

        } catch (DateTimeParseException e) {
            // 날짜 형식이 yyyy-MM-dd가 아닌 경우
            throw new GeneralException(AccountErrorCode.INVALID_DATE_FORMAT);
        }
    }

}
