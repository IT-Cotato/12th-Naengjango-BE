package com.itcotato.naengjango.domain.member.controller;

import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import com.itcotato.naengjango.global.apiPayload.code.GeneralSuccessCode;
import com.itcotato.naengjango.domain.member.dto.MyPageDto;
import com.itcotato.naengjango.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MemberService memberService;

    @Operation(
            summary = "마이페이지 내 정보 조회 API by 이정환 (개발 중)",
            description = "인증된 사용자의 내 정보(이름, loginId, 전화번호, 예산, 소셜타입, role)를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/me")
    public ApiResponse<MyPageDto.MeResponse> me(@RequestAttribute(value = "memberId", required = false) Long memberId) {
        if (memberId == null) throw new IllegalArgumentException("Missing request attribute: memberId");
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.getMe(memberId));
    }

    @Operation(
            summary = "마이페이지 예산 조회 API by 이정환 (개발 중)",
            description = "인증된 사용자의 예산(budget)을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/budget")
    public ApiResponse<MyPageDto.BudgetResponse> budget(@RequestAttribute("memberId") Long memberId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.getBudget(memberId));
    }

    @Operation(
            summary = "마이페이지 예산 수정 API by 이정환 (개발 중)",
            description = "인증된 사용자의 예산(budget)을 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검증 실패)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/budget")
    public ApiResponse<MyPageDto.BudgetResponse> updateBudget(
            @RequestAttribute("memberId") Long memberId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "수정할 예산 정보",
                    content = @Content(schema = @Schema(implementation = MyPageDto.UpdateBudgetRequest.class))
            )
            @Valid @RequestBody MyPageDto.UpdateBudgetRequest request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.updateBudget(memberId, request));
    }
    @Operation(summary = "이용약관 조회", description = "앱 내 이용약관 텍스트/버전 정보를 조회합니다. (비로그인 가능)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/policies/terms")
    public ApiResponse<MyPageDto.PolicyResponse> terms() {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.getTermsPolicy());
    }

    @Operation(summary = "개인정보 처리방침 조회", description = "앱 내 개인정보 처리방침 텍스트/버전 정보를 조회합니다. (비로그인 가능)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/policies/privacy")
    public ApiResponse<MyPageDto.PolicyResponse> privacy() {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.getPrivacyPolicy());
    }

    // ===== 추가: FAQ =====

    @Operation(summary = "FAQ 목록 조회", description = "FAQ 목록을 조회합니다. (비로그인 가능)")
    @GetMapping("/faqs")
    public ApiResponse<MyPageDto.FaqListResponse> faqs(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.getFaqs(category, keyword, page, size));
    }

    @Operation(summary = "FAQ 상세 조회", description = "FAQ 질문/답변을 조회합니다. (비로그인 가능)")
    @GetMapping("/faqs/{faqId}")
    public ApiResponse<MyPageDto.FaqDetailResponse> faqDetail(@PathVariable Long faqId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.getFaqDetail(faqId));
    }

    // ===== 추가: 문의하기 =====

    @Operation(summary = "문의하기 등록", description = "인증된 사용자가 1:1 문의를 등록합니다.")
    @PostMapping("/inquiries")
    public ApiResponse<MyPageDto.InquiryCreateResponse> createInquiry(
            @RequestAttribute("memberId") Long memberId,
            @Valid @RequestBody MyPageDto.InquiryCreateRequest request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.createInquiry(memberId, request));
    }

    @Operation(summary = "내 문의 목록 조회", description = "인증된 사용자의 문의 목록을 조회합니다.")
    @GetMapping("/inquiries/me")
    public ApiResponse<MyPageDto.InquiryListResponse> myInquiries(
            @RequestAttribute("memberId") Long memberId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.getMyInquiries(memberId, page, size));
    }

    @Operation(summary = "내 문의 상세 조회", description = "인증된 사용자의 문의 상세(답변 포함)를 조회합니다.")
    @GetMapping("/inquiries/{inquiryId}")
    public ApiResponse<MyPageDto.InquiryDetailResponse> myInquiryDetail(
            @RequestAttribute("memberId") Long memberId,
            @PathVariable Long inquiryId
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.getMyInquiryDetail(memberId, inquiryId));
    }

//    회원 탈퇴
    @PostMapping("/withdrawal")
    public ApiResponse<MyPageDto.WithdrawResponse> withdraw(
            @RequestAttribute("memberId") Long memberId,
            @Valid @RequestBody(required = false) MyPageDto.WithdrawRequest request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, memberService.withdraw(memberId, request));
    }

}
