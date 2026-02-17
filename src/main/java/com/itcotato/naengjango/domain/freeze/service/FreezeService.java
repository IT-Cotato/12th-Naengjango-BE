package com.itcotato.naengjango.domain.freeze.service;

import com.itcotato.naengjango.domain.account.service.TransactionService;
import com.itcotato.naengjango.domain.freeze.dto.FreezeRequestDto;
import com.itcotato.naengjango.domain.freeze.dto.FreezeResponseDto;
import com.itcotato.naengjango.domain.freeze.entity.FreezeItem;
import com.itcotato.naengjango.domain.freeze.enums.FreezeStatus;
import com.itcotato.naengjango.domain.freeze.exception.FreezeException;
import com.itcotato.naengjango.domain.freeze.exception.code.FreezeErrorCode;
import com.itcotato.naengjango.domain.freeze.repository.FreezeItemRepository;
import com.itcotato.naengjango.domain.igloo.dto.IglooResponseDto;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.domain.igloo.service.IglooService;
import com.itcotato.naengjango.domain.snowball.service.SnowballService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FreezeService {

    private final FreezeItemRepository freezeItemRepository;
    private final TransactionService transactionService;
    private final SnowballService snowballService;
    private final IglooService iglooService;

    /**
     * 냉동 생성 (수동 등록)
     */
    @Transactional
    public FreezeResponseDto.Create create(
            Member member,
            FreezeRequestDto.Create request
    ) {
        validateCreate(request);

        FreezeItem item = FreezeItem.create(
                member,
                request.appName(),
                request.itemName(),
                request.price()
        );

        freezeItemRepository.save(item);

        return new FreezeResponseDto.Create(
                item.getId(),
                item.getExpiresAt()
        );
    }

    /**
     * 냉동 중 목록 조회 (최신순 / 가격순)
     */
    @Transactional(readOnly = true)
    public List<FreezeResponseDto.Item> getFrozenItems(
            Member member,
            String sort
    ) {
        List<FreezeItem> items =
                freezeItemRepository.findByMemberAndStatus(member, FreezeStatus.FROZEN);

        if ("price".equalsIgnoreCase(sort)) {
            items.sort((a, b) -> Long.compare(b.getPrice(), a.getPrice()));
        } else {
            items.sort((a, b) -> b.getFrozenAt().compareTo(a.getFrozenAt()));
        }

        LocalDateTime now = LocalDateTime.now();

        return items.stream()
                .map(item -> new FreezeResponseDto.Item(
                        item.getId(),
                        item.getAppName(),
                        item.getItemName(),
                        item.getPrice(),
                        item.getFrozenAt(),
                        item.getExpiresAt(),
                        Math.max(0, Duration.between(now, item.getExpiresAt()).getSeconds())
                ))
                .toList();
    }

    /**
     * 냉동 기록 수정 (FROZEN만 가능)
     */
    @Transactional
    public void update(
            Member member,
            Long freezeId,
            FreezeRequestDto.Update request
    ) {
        validateUpdate(request);

        FreezeItem item = findOwnedFreeze(freezeId, member);

        if (item.getStatus() != FreezeStatus.FROZEN) {
            throw new FreezeException(FreezeErrorCode.FREEZE_INVALID_STATUS);
        }

        item.update(
                request.appName(),
                request.itemName(),
                request.price()
        );
    }

    /**
     * 계속 냉동 (24H 연장, 다중)
     */
    @Transactional
    public FreezeResponseDto.BulkAction extend(
            Member member,
            List<Long> freezeIds
    ) {
        List<FreezeItem> items = findOwnedFreezes(freezeIds, member);

        for (FreezeItem item : items) {
            if (item.getStatus() != FreezeStatus.FROZEN) {
                throw new FreezeException(FreezeErrorCode.FREEZE_INVALID_STATUS);
            }
            item.extend24Hours();
        }

        return new FreezeResponseDto.BulkAction(
                new FreezeResponseDto.ActionResult(items.size(), 0),
                null
        );
    }

    /**
     * 냉동 실패 (다중)
     */
    @Transactional
    public FreezeResponseDto.BulkAction fail(Member member, List<Long> freezeIds) {
        List<FreezeItem> items = findOwnedFreezes(freezeIds, member);

        int failCount = 0;

        for (FreezeItem item : items) {
            if (item.getStatus() != FreezeStatus.FROZEN) {
                throw new FreezeException(FreezeErrorCode.FREEZE_INVALID_STATUS);
            }
            item.markFailed();

            // 실패 처리 시 해당 항목 가격만큼 지출 기록 생성
            transactionService.createFreezeExpense(
                    member,
                    item.getPrice(),
                    item.getItemName()
            );

            failCount++;
        }

        // 실패 누적 반영 + 하락/방어 처리
        IglooResponseDto.FailCheckResult result =
                iglooService.checkFailures(member, failCount);


        int currentBalance = snowballService.getBalance(member);

        FreezeResponseDto.StatusSnapshot snapshot =
                new FreezeResponseDto.StatusSnapshot(
                        currentBalance,
                        false,
                        0,
                        result.thresholdReached(),
                        result.canProtect()
                );

        return new FreezeResponseDto.BulkAction(
                new FreezeResponseDto.ActionResult(failCount, 0),
                snapshot
        );
    }

    /**
     * 냉동 성공 (다중) + 눈덩이 지급(기본 2개 제한) + 3일 연속 보너스
     */
    @Transactional
    public FreezeResponseDto.BulkAction success(Member member, List<Long> freezeIds) {
        List<FreezeItem> items = findOwnedFreezes(freezeIds, member);

        int successCount = 0;

        for (FreezeItem item : items) {
            if (item.getStatus() != FreezeStatus.FROZEN) {
                throw new FreezeException(FreezeErrorCode.FREEZE_INVALID_STATUS);
            }
            item.markSuccess();
            successCount++;
        }

        int snowballsGranted = 0;

        // =========================
        // 기본 지급: 하루 최대 2개
        // =========================
        int todayBasicEarned = snowballService.getTodayBasicEarned(member); // reason=FREEZE_SUCCESS 합
        int canEarn = Math.max(0, 2 - todayBasicEarned);
        int basicEarn = Math.min(successCount, canEarn);

        for (int i = 0; i < basicEarn; i++) {
            snowballService.earn(member, 1, SnowballService.REASON_FREEZE_SUCCESS);
            snowballsGranted++;
        }

        // =========================
        // 보너스: 연속 3일 성공이면 +2 (기본 제한과 무관)
        // =========================
        int streakDays = getCurrentStreakDays(member);
        boolean isStreak = streakDays > 0;

        if (streakDays == 3) {
            snowballService.earn(member, 2, SnowballService.REASON_FREEZE_STREAK_3_DAYS);
            snowballsGranted += 2;
        }

        int balance = snowballService.getBalance(member);

        return new FreezeResponseDto.BulkAction(
                new FreezeResponseDto.ActionResult(
                        successCount,
                        snowballsGranted
                ),
                new FreezeResponseDto.StatusSnapshot(
                        balance,
                        streakDays > 0,
                        streakDays,
                        false,
                        false
                )
        );
    }

    /**
     * 선택 항목 구매 시 이번 달 하루 예산 계산
     */
    @Transactional(readOnly = true)
    public FreezeResponseDto.BudgetPreview budgetPreview(
            Member member,
            List<Long> freezeIds
    ) {
        List<FreezeItem> items = findOwnedFreezes(freezeIds, member);

        Long totalPrice = items.stream()
                .mapToLong(FreezeItem::getPrice)
                .sum();

        int remainingDays = remainingDaysInMonthInclusive(LocalDate.now());
        Long perDayBudget = remainingDays <= 0
                ? totalPrice
                : totalPrice / remainingDays;

        return new FreezeResponseDto.BudgetPreview(
                totalPrice,
                remainingDays,
                perDayBudget
        );
    }

    /* ===================== private helpers ===================== */

    private FreezeItem findOwnedFreeze(Long freezeId, Member member) {
        FreezeItem item = freezeItemRepository.findById(freezeId)
                .orElseThrow(() -> new FreezeException(FreezeErrorCode.FREEZE_NOT_FOUND));

        if (!item.getMember().getId().equals(member.getId())) {
            throw new FreezeException(FreezeErrorCode.FREEZE_INVALID_USER);
        }
        return item;
    }

    private List<FreezeItem> findOwnedFreezes(List<Long> ids, Member member) {
        List<FreezeItem> items =
                freezeItemRepository.findByIdInAndMember(ids, member);

        if (items.size() != ids.size()) {
            throw new FreezeException(FreezeErrorCode.FREEZE_INVALID_USER);
        }
        return items;
    }

    private void validateCreate(FreezeRequestDto.Create request) {
        if (request.appName() == null || request.appName().isBlank())
            throw new FreezeException(FreezeErrorCode.FREEZE_INVALID_APPNAME);
        if (request.appName().length() > 64)
            throw new FreezeException(FreezeErrorCode.FREEZE_INVALID_APPNAME);

        if (request.itemName() == null || request.itemName().isBlank())
            throw new FreezeException(FreezeErrorCode.FREEZE_INVALID_ITEM);
        if (request.itemName().length() > 64)
            throw new FreezeException(FreezeErrorCode.FREEZE_INVALID_ITEM);

        if (request.price() <= 0 || request.price() > 9_999_999)
            throw new FreezeException(FreezeErrorCode.FREEZE_INVALID_PRICE);
    }

    private void validateUpdate(FreezeRequestDto.Update request) {
        validateCreate(
                new FreezeRequestDto.Create(
                        request.appName(),
                        request.itemName(),
                        request.price()
                )
        );
    }

    /**
     * 현재 연속 성공 며칠째인지 반환
     */
    private int getCurrentStreakDays(Member member) {
        // 최근 성공 날짜들을 최신순으로 조회
        List<LocalDate> successDates =
                freezeItemRepository.findRecentSuccessDecidedAt(
                                member,
                                PageRequest.of(0, 30) // 넉넉하게
                        ).stream()
                        .map(LocalDateTime::toLocalDate)
                        .toList();

        if (successDates.isEmpty()) return 0;

        int streak = 1; // 첫 날은 무조건 카운트
        LocalDate prev = successDates.get(0);

        for (int i = 1; i < successDates.size(); i++) {
            LocalDate curr = successDates.get(i);

            if (prev.minusDays(1).equals(curr)) {
                streak++;
                prev = curr;
            } else {
                break; // 연속 끊기면 종료
            }
        }

        return streak;
    }

    private int remainingDaysInMonthInclusive(LocalDate today) {
        LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());
        return (int) (ChronoUnit.DAYS.between(today, lastDay) + 1);
    }

    @Transactional(readOnly = true)
    public FreezeResponseDto.Detail getFreeze(Member member, Long freezeId) {
        FreezeItem item = freezeItemRepository.findById(freezeId)
                .orElseThrow(() -> new FreezeException(FreezeErrorCode.FREEZE_NOT_FOUND));

        if (!item.getMember().getId().equals(member.getId())) {
            throw new FreezeException(FreezeErrorCode.FREEZE_FORBIDDEN);
        }

        return new FreezeResponseDto.Detail(
                item.getId(),
                item.getAppName(),
                item.getItemName(),
                item.getPrice(),
                item.getFrozenAt(),
                item.getExpiresAt(),
                item.getUpdatedAt()
        );
    }
}