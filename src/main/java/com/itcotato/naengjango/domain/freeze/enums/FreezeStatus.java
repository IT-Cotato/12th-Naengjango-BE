package com.itcotato.naengjango.domain.freeze.enums;

public enum FreezeStatus {
    FROZEN,      // 냉동 중
    AVAILABLE,   // 해동 가능 상태 (기한 도달)
    PURCHASED,   // 구매 완료
    SUCCESS    // 취소
}
