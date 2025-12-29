package com.itcotato.naengjango.domain.auth.exception;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import com.itcotato.naengjango.global.exception.GeneralException;

public class AuthException extends GeneralException {
    public AuthException(BaseErrorCode code) { super(code); }
}
