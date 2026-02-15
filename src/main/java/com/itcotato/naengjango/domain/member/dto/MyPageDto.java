package com.itcotato.naengjango.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


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
            String role,

            @Schema(description = "가입일시", example = "2026-02-01T12:34:56")
            LocalDateTime createdAt) {}

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

    // =========================
    // 추가: 이용약관 / 개인정보 처리방침
    // (피그마: '이용약관', '개인정보 처리 방침' 화면의 본문을 서버가 내려주는 경우)
    // =========================

    @Schema(name = "PolicyContentType", description = "정책 문서 본문 포맷 타입")
    public enum PolicyContentType {
        @Schema(description = "일반 텍스트")
        TEXT,

        @Schema(description = "마크다운")
        MARKDOWN,

        @Schema(description = "HTML")
        HTML,

        @Schema(description = "URL만 제공(앱은 WebView로 오픈)")
        URL
    }

    @Builder
    @Schema(name = "PolicyResponse", description = "이용약관/개인정보처리방침 조회 응답 DTO")
    public record PolicyResponse(
            @Schema(description = "문서 제목", example = "이용약관")
            String title,

            @Schema(description = "버전", example = "v1.0")
            String version,

            @Schema(description = "본문 타입", example = "MARKDOWN")
            PolicyContentType contentType,

            @Schema(description = "본문(또는 URL)", example = "## 제1조 ... 또는 https://...")
            String content,

            @Schema(description = "최종 수정일시", example = "2026-02-04T01:30:00")
            LocalDateTime updatedAt
    ) {}

    // =========================
    // 추가: FAQ (목록/상세)
    // (피그마: FAQ 리스트 + 클릭 시 답변 모달/바텀시트)
    // =========================

    @Builder
    @Schema(name = "FaqSummary", description = "FAQ 목록 아이템 DTO")
    public record FaqSummary(
            @Schema(description = "FAQ ID", example = "1")
            Long faqId,

            @Schema(description = "카테고리", example = "계정/로그인")
            String category,

            @Schema(description = "질문", example = "자주 묻는 질문인가요?")
            String question,

            @Schema(description = "상단 고정 여부", example = "false")
            Boolean pinned,

            @Schema(description = "정렬 순서(작을수록 위)", example = "10")
            Integer order
    ) {}

    @Builder
    @Schema(name = "FaqListResponse", description = "FAQ 목록 조회 응답 DTO")
    public record FaqListResponse(
            @Schema(description = "FAQ 목록")
            List<FaqSummary> items,

            @Schema(description = "현재 페이지(0부터)", example = "0")
            Integer page,

            @Schema(description = "페이지 크기", example = "20")
            Integer size,

            @Schema(description = "전체 요소 수", example = "57")
            Long totalElements,

            @Schema(description = "전체 페이지 수", example = "3")
            Integer totalPages
    ) {}

    @Builder
    @Schema(name = "FaqDetailResponse", description = "FAQ 상세 조회 응답 DTO")
    public record FaqDetailResponse(
            @Schema(description = "FAQ ID", example = "1")
            Long faqId,

            @Schema(description = "카테고리", example = "계정/로그인")
            String category,

            @Schema(description = "질문", example = "자주 묻는 질문인가요?")
            String question,

            @Schema(description = "답변", example = "답변은 이렇게 제공됩니다.")
            String answer,

            @Schema(description = "최종 수정일시", example = "2026-02-04T01:30:00")
            LocalDateTime updatedAt
    ) {}

    // =========================
    // 추가: 문의하기 (등록/내 문의 목록/내 문의 상세)
    // (피그마: 문의하기 제목/내용 입력 후 제출)
    // =========================

    @Schema(name = "InquiryStatus", description = "문의 상태")
    public enum InquiryStatus {
        @Schema(description = "접수됨")
        RECEIVED,

        @Schema(description = "처리중")
        IN_PROGRESS,

        @Schema(description = "답변완료")
        ANSWERED,

        @Schema(description = "종료")
        CLOSED
    }

    @Builder
    @Schema(name = "InquiryCreateRequest", description = "문의 등록 요청 DTO")
    public record InquiryCreateRequest(
            @NotBlank
            @Size(max = 100)
            @Schema(description = "문의 제목", example = "로그인이 안돼요", requiredMode = Schema.RequiredMode.REQUIRED)
            String title,

            @NotBlank
            @Size(max = 5000)
            @Schema(description = "문의 내용", example = "앱에서 로그인 시 오류가 발생합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
            String content
    ) {}

    @Builder
    @Schema(name = "InquiryCreateResponse", description = "문의 등록 응답 DTO")
    public record InquiryCreateResponse(
            @Schema(description = "문의 ID", example = "101")
            Long inquiryId,

            @Schema(description = "문의 상태", example = "RECEIVED")
            InquiryStatus status,

            @Schema(description = "등록일시", example = "2026-02-04T01:30:00")
            LocalDateTime createdAt
    ) {}

    @Builder
    @Schema(name = "InquirySummary", description = "내 문의 목록 아이템 DTO")
    public record InquirySummary(
            @Schema(description = "문의 ID", example = "101")
            Long inquiryId,

            @Schema(description = "문의 제목", example = "로그인이 안돼요")
            String title,

            @Schema(description = "문의 상태", example = "IN_PROGRESS")
            InquiryStatus status,

            @Schema(description = "등록일시", example = "2026-02-04T01:30:00")
            LocalDateTime createdAt
    ) {}

    @Builder
    @Schema(name = "InquiryListResponse", description = "내 문의 목록 조회 응답 DTO")
    public record InquiryListResponse(
            @Schema(description = "내 문의 목록")
            List<InquirySummary> items,

            @Schema(description = "현재 페이지(0부터)", example = "0")
            Integer page,

            @Schema(description = "페이지 크기", example = "20")
            Integer size,

            @Schema(description = "전체 요소 수", example = "4")
            Long totalElements,

            @Schema(description = "전체 페이지 수", example = "1")
            Integer totalPages
    ) {}

    @Builder
    @Schema(name = "InquiryDetailResponse", description = "내 문의 상세 조회 응답 DTO")
    public record InquiryDetailResponse(
            @Schema(description = "문의 ID", example = "101")
            Long inquiryId,

            @Schema(description = "문의 제목", example = "로그인이 안돼요")
            String title,

            @Schema(description = "문의 내용", example = "앱에서 로그인 시 오류가 발생합니다.")
            String content,

            @Schema(description = "문의 상태", example = "ANSWERED")
            InquiryStatus status,

            @Schema(description = "답변 내용(없으면 null)", example = "안녕하세요. 조치 방법은 ...")
            String answer,

            @Schema(description = "등록일시", example = "2026-02-04T01:30:00")
            LocalDateTime createdAt,

            @Schema(description = "답변일시(없으면 null)", example = "2026-02-04T02:10:00")
            LocalDateTime answeredAt
    ) {}

    // 회원탈퇴 관련

    @Schema(name = "WithdrawRequest", description = "회원탈퇴 요청 DTO")
    public record WithdrawRequest(
            @Schema(description = "탈퇴 사유(선택)", example = "서비스를 잘 사용하지 않아요")
            @Size(max = 500)
            String reason
    ) {}

    @Schema(name = "WithdrawResponse", description = "회원탈퇴 응답 DTO")
    @Getter
    @Builder
    @AllArgsConstructor
    public static class WithdrawResponse {
        @Schema(description = "처리 메시지", example = "탈퇴 처리 완료")
        private String message;
    }

    // ===== 고정지출 조회/수정 =====
    public record FixedExpenditureItem(
            @jakarta.validation.constraints.NotBlank
            String item,

            @jakarta.validation.constraints.NotNull
            @jakarta.validation.constraints.PositiveOrZero
            Long amount
    ) {}

    public record UpdateFixedExpendituresRequest(
            @jakarta.validation.constraints.NotNull
            java.util.List<FixedExpenditureItem> items
    ) {}

    @Getter
    @lombok.Builder
    public static class FixedExpendituresResponse {
        private java.util.List<FixedExpenditureItem> items;

    }

    // 비밀번호 변경 관련
    public record PasswordChangeRequest(
            @jakarta.validation.constraints.NotBlank String currentPassword,
            @jakarta.validation.constraints.NotBlank String newPassword,
            @jakarta.validation.constraints.NotBlank String newPasswordConfirm
    ) {}


}
