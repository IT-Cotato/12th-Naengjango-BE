package com.itcotato.naengjango.domain.member.exception;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import com.itcotato.naengjango.global.exception.GeneralException;

public class MemberException extends GeneralException {
    public MemberException(BaseErrorCode code) {
        super(code);
    }
}
