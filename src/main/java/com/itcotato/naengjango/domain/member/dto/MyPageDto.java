package com.itcotato.naengjango.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Schema(name = "MyPageDto", description = "마이페이지 관련 DTO 모음")
public class MyPageDto {

    @Builder
    @Schema(name = "MeResponse", description = "내 정보 조회 응답 DTO")
    public record MeResponse(
            @Schema(description = "회원 ID", example = "12")
            Long id,

            @Schema(description = "회원 이름", example = "홍길동")
            String name,

            @Schema(description = "로그인 아이디", example = "jhlee123")
            String loginId,

            @Schema(description = "휴대폰 번호", example = "010-1234-5678")
            String phoneNumber,

            @Schema(description = "예산", example = "300000")
            Integer budget,

            @Schema(description = "소셜 타입", example = "KAKAO")
            String socialType,

            @Schema(description = "권한/역할", example = "ROLE_USER")
            String role
    ) {}

    @Builder
    @Schema(name = "UpdateBudgetRequest", description = "예산 수정 요청 DTO")
    public record UpdateBudgetRequest(
            @NotNull
            @Min(0)
            @Max(2_000_000_000)
            @Schema(description = "수정할 예산 (0 ~ 2,000,000,000)", example = "500000", requiredMode = Schema.RequiredMode.REQUIRED)
            Integer budget
    ) {}

    @Builder
    @Schema(name = "BudgetResponse", description = "예산 조회/수정 응답 DTO")
    public record BudgetResponse(
            @Schema(description = "현재 예산", example = "500000")
            Integer budget
    ) {}


}
