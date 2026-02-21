package com.itcotato.naengjango.domain.igloo.exception;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import com.itcotato.naengjango.global.exception.GeneralException;

public class IglooException extends GeneralException {
    public IglooException(BaseErrorCode code) {
        super(code);
    }
}
