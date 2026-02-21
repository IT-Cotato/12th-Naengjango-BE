package com.itcotato.naengjango.domain.snowball.exception;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import com.itcotato.naengjango.global.exception.GeneralException;

public class SnowballException extends GeneralException {
    public SnowballException(BaseErrorCode code) {super(code);}
}
