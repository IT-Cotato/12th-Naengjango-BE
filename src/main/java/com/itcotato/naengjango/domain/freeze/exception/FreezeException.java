package com.itcotato.naengjango.domain.freeze.exception;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import com.itcotato.naengjango.global.exception.GeneralException;

public class FreezeException extends GeneralException {
  public FreezeException(BaseErrorCode code) {
    super(code);
  }
}
