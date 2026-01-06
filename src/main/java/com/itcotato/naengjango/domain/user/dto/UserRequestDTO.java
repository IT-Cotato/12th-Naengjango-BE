package com.itcotato.naengjango.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

public class UserRequestDTO {

    @Getter
    @Schema(description = "회원가입 요청 객체")
    public static class SignupDTO {
        @Schema(description = "이름", example = "홍길동")
        private String name;

        @Schema(description = "전화번호", example = "01012345678")
        private String phoneNumber;

        @Schema(description = "아이디", example = "user123")
        private String loginId;

        @Schema(description = "비밀번호", example = "password123!")
        private String password;

        @Schema(description = "동의한 약관 ID 리스트", example = "[1, 2, 3]")
        private List<Long> agreedAgreementIds;

        @Schema(description = "한 달 예산", example = "600000")
        private Integer budget;

        @Schema(description = "고정 지출 리스트")
        private List<FixedExpenditureDTO> fixedExpenditures;
    }

    @Getter
    public static class SmsSendDTO {
        private String phoneNumber;
    }

    @Getter
    public static class FixedExpenditureDTO {
        @Schema(description = "지출 항목명", example = "월세")
        private String item;

        @Schema(description = "지출 금액", example = "500000")
        private Long amount;
    }
}