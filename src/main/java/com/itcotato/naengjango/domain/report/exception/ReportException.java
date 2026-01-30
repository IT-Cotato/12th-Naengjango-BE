package com.itcotato.naengjango.domain.report.exception;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import com.itcotato.naengjango.global.exception.GeneralException;

public class ReportException extends GeneralException {
    public ReportException(BaseErrorCode code) {
        super(code);
    }
}
