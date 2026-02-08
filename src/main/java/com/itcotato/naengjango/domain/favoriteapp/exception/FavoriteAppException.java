package com.itcotato.naengjango.domain.favoriteapp.exception;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import com.itcotato.naengjango.global.exception.GeneralException;

public class FavoriteAppException extends GeneralException {
    public FavoriteAppException(BaseErrorCode code) {
        super(code);
    }
}
