package com.itcotato.naengjango.domain.freeze.service.command;

import com.itcotato.naengjango.domain.freeze.dto.FreezeRequestDto;
import com.itcotato.naengjango.domain.freeze.dto.FreezeResponseDto;
import com.itcotato.naengjango.domain.freeze.entity.FreezeItem;
import com.itcotato.naengjango.domain.freeze.enums.FreezeStatus;
import com.itcotato.naengjango.domain.freeze.exception.FreezeException;
import com.itcotato.naengjango.domain.freeze.exception.code.FreezeErrorCode;
import com.itcotato.naengjango.domain.freeze.repository.FreezeItemRepository;
import com.itcotato.naengjango.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FreezeCommandService {

    private final FreezeItemRepository freezeItemRepository;
    private final CurrentMemberProvider currentMemberProvider;

    /**
     * 냉동 등록
     */
    public FreezeResponseDto.CreateResponse create(
            FreezeRequestDto.CreateRequest request
    ) {
        Member me = currentMemberProvider.getCurrentMember();

        FreezeItem item = FreezeItem.create(
                me,
                request.appName(),
                request.itemName(),
                request.price(),
                null
        );

        FreezeItem saved = freezeItemRepository.save(item);

        return new FreezeResponseDto.CreateResponse(
                saved.getId(),
                saved.getItemName(),
                saved.getPrice(),
                saved.getStatus(),
                saved.getFrozenAt(),
                saved.getDeadline()
        );
    }

    /**
     * 구매 확정
     */
    public void purchase(Long freezeId) {
        FreezeItem item = findOwnedFreeze(freezeId);
        item.purchase();
    }

    /**
     * 구매 취소
     */
    public void cancel(Long freezeId) {
        FreezeItem item = findOwnedFreeze(freezeId);
        item.cancel();
    }

    /**
     * 삭제
     */
    public void delete(Long freezeId) {
        FreezeItem item = findOwnedFreeze(freezeId);
        freezeItemRepository.delete(item);
    }

    /**
     * 수정 (FROZEN 상태만 가능)
     */
    public FreezeResponseDto.DetailResponse update(
            Long freezeId,
            FreezeRequestDto.UpdateRequest request
    ) {
        FreezeItem item = findOwnedFreeze(freezeId);

        if (item.getStatus() != FreezeStatus.FROZEN) {
            throw new FreezeException(FreezeErrorCode.INVALID_STATUS_TRANSITION);
        }

        item.update(
                request.appName(),
                request.itemName(),
                request.price()
        );

        return new FreezeResponseDto.DetailResponse(
                item.getId(),
                item.getAppName(),
                item.getItemName(),
                item.getPrice(),
                item.getStatus(),
                item.getFrozenAt(),
                item.getDeadline()
        );
    }
}