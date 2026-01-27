package com.itcotato.naengjango.domain.freeze.service.command;

import com.itcotato.naengjango.domain.freeze.repository.FreezeItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FreezeCommandService {

    private final FreezeItemRepository freezeItemRepository;
    private final CurrentMemberProvider currentMemberProvider;

    public FreezeCreateResponse createFreezeItem(FreezeCreateRequest request) {
        Member me = currentMemberProvider.getCurrentMember();

        FreezeItem item = FreezeItem.create(
                me,
                request.appName(),
                request.itemName(),
                request.price(),
                request.deadline()
        );

        FreezeItem saved = freezeItemRepository.save(item);
        return new FreezeCreateResponse(saved.getId());
    }

    public FreezeStatusResponse makeAvailable(Long freezeItemId) {
        Member me = currentMemberProvider.getCurrentMember();
        FreezeItem item = findAndValidate(me, freezeItemId);

        try {
            item.makeAvailable();
        } catch (IllegalStateException e) {
            throw new FreezeException(FreezeErrorCode.INVALID_STATUS_TRANSITION);
        }

        return new FreezeStatusResponse(item.getId(), item.getStatus());
    }

    public FreezeStatusResponse purchase(Long freezeItemId) {
        Member me = currentMemberProvider.getCurrentMember();
        FreezeItem item = findAndValidate(me, freezeItemId);

        if (item.getStatus() == FreezeStatus.PURCHASED) {
            throw new FreezeException(FreezeErrorCode.ALREADY_PURCHASED);
        }
        if (item.getStatus() == FreezeStatus.CANCELLED) {
            throw new FreezeException(FreezeErrorCode.ALREADY_CANCELLED);
        }

        try {
            item.purchase();
        } catch (IllegalStateException e) {
            throw new FreezeException(FreezeErrorCode.INVALID_STATUS_TRANSITION);
        }

        return new FreezeStatusResponse(item.getId(), item.getStatus());
    }

    public FreezeStatusResponse cancel(Long freezeItemId) {
        Member me = currentMemberProvider.getCurrentMember();
        FreezeItem item = findAndValidate(me, freezeItemId);

        if (item.getStatus() == FreezeStatus.CANCELLED) {
            throw new FreezeException(FreezeErrorCode.ALREADY_CANCELLED);
        }

        try {
            item.cancel();
        } catch (IllegalStateException e) {
            throw new FreezeException(FreezeErrorCode.INVALID_STATUS_TRANSITION);
        }

        return new FreezeStatusResponse(item.getId(), item.getStatus());
    }

    private FreezeItem findAndValidate(Member me, Long freezeItemId) {
        FreezeItem item = freezeItemRepository.findById(freezeItemId)
                .orElseThrow(() -> new FreezeException(FreezeErrorCode.FREEZE_ITEM_NOT_FOUND));

        if (!item.getMember().getId().equals(me.getId())) {
            throw new FreezeException(FreezeErrorCode.FREEZE_FORBIDDEN);
        }

        return item;
    }
}