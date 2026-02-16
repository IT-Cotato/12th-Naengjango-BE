package com.itcotato.naengjango.domain.member.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AgreementResponseDto {

    public record AgreeResult(
            Long agreementId,
            boolean agreed,
            LocalDateTime agreedAt
    ) {}

    public record AgreeListResult(
            List<AgreeResult> agreements
    ) {}

}
