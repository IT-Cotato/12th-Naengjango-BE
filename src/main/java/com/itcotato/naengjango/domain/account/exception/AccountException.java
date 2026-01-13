package com.itcotato.naengjango.domain.account.exception;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import com.itcotato.naengjango.global.exception.GeneralException;

public class AccountException extends GeneralException {
    public AccountException(BaseErrorCode code) {
        super(code);
    }
}
