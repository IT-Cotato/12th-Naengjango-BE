package com.itcotato.naengjango.domain.member.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 회원 관련 예외 코드 정의
 */
@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
    // 아이디 중복 확인 관련 에러 코드
    MEMBER_ID_ALREADY_EXISTS(HttpStatus.BAD_REQUEST,
            "MEMBER400_1",
            "이미 존재하는 아이디입니다."),

    ALREADY_SOCIAL_REGISTERED(HttpStatus.BAD_REQUEST,
            "MEMBER400_2",
            "이미 가입된 계정입니다."),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND,
            "MEMBER404_1",
            "회원을 찾을 수 없습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
