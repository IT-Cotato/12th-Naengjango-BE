package com.itcotato.naengjango.domain.member.dto;

import java.util.List;

public class AgreementRequestDto {

    public record AgreeRequest(
            List<AgreementItem> agreements
    ) {}

    public record AgreementItem(
            Long agreementId,
            boolean agreed
    ) {}
}
