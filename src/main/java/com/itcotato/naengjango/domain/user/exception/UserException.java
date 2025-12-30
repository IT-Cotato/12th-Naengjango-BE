package com.itcotato.naengjango.domain.user.exception;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import com.itcotato.naengjango.global.exception.GeneralException;

public class UserException extends GeneralException {
    public UserException(BaseErrorCode code) {
        super(code);
    }
}
