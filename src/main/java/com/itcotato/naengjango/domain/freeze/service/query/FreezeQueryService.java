package com.itcotato.naengjango.domain.freeze.service.query;

import com.itcotato.naengjango.domain.freeze.dto.FreezeResponseDto;
import com.itcotato.naengjango.domain.freeze.entity.FreezeItem;
import com.itcotato.naengjango.domain.freeze.exception.FreezeException;
import com.itcotato.naengjango.domain.freeze.exception.code.FreezeErrorCode;
import com.itcotato.naengjango.domain.freeze.repository.FreezeItemRepository;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.global.security.CurrentMemberProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FreezeQueryService {

    private final FreezeItemRepository freezeItemRepository;
    private final CurrentMemberProvider currentMemberProvider;

    /**
     * 냉동 목록 조회
     */
    public List<FreezeResponseDto.ListResponse> getFreezeList() {
        Member me = currentMemberProvider.getCurrentMember();

        return freezeItemRepository.findAllByMemberOrderByFrozenAtDesc(me)
                .stream()
                .map(item -> new FreezeResponseDto.ListResponse(
                        item.getId(),
                        item.getItemName(),
                        item.getPrice(),
                        item.getStatus(),
                        item.getDeadline()
                ))
                .toList();
    }

    /**
     * 냉동 상세 조회
     */
    public FreezeResponseDto.DetailResponse getFreezeDetail(Long freezeId) {
        Member me = currentMemberProvider.getCurrentMember();
        FreezeItem item = findOwnedFreeze(me, freezeId);

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

    private FreezeItem findOwnedFreeze(Member me, Long freezeId) {
        FreezeItem item = freezeItemRepository.findById(freezeId)
                .orElseThrow(() ->
                        new FreezeException(FreezeErrorCode.FREEZE_ITEM_NOT_FOUND));

        if (!item.getMember().getId().equals(me.getId())) {
            throw new FreezeException(FreezeErrorCode.FREEZE_FORBIDDEN);
        }
        return item;
    }
}