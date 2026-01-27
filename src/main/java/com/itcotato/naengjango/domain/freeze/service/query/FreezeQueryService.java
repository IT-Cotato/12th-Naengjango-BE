package com.itcotato.naengjango.domain.freeze.service.query;

import com.itcotato.naengjango.domain.freeze.repository.FreezeItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FreezeQueryService {

    private final FreezeItemRepository freezeItemRepository;
    private final CurrentMemberProvider currentMemberProvider;

    public List<FreezeItemResponse> getMyFreezeItems(FreezeStatus status) {
        Member me = currentMemberProvider.getCurrentMember();

        List<FreezeItem> items = (status == null)
                ? freezeItemRepository.findAllByMemberOrderByFrozenAtDesc(me)
                : freezeItemRepository.findAllByMemberAndStatusOrderByFrozenAtDesc(me, status);

        return items.stream()
                .map(this::toResponse)
                .toList();
    }

    public FreezeItemResponse getMyFreezeItemDetail(Long freezeItemId) {
        Member me = currentMemberProvider.getCurrentMember();
        FreezeItem item = freezeItemRepository.findById(freezeItemId)
                .orElseThrow(() -> new FreezeException(FreezeErrorCode.FREEZE_ITEM_NOT_FOUND));

        validateOwnership(me, item);
        return toResponse(item);
    }

    private void validateOwnership(Member me, FreezeItem item) {
        if (!item.getMember().getId().equals(me.getId())) {
            throw new FreezeException(FreezeErrorCode.FREEZE_FORBIDDEN);
        }
    }

    private FreezeItemResponse toResponse(FreezeItem item) {
        return new FreezeItemResponse(
                item.getId(),
                item.getAppName(),
                item.getItemName(),
                item.getPrice(),
                item.getFrozenAt(),
                item.getDeadline(),
                item.getStatus()
        );
    }
}