package com.itcotato.naengjango.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itcotato.naengjango.member.dto.MyPageDto;
import com.itcotato.naengjango.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MyPageController.class)
class MyPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("GET /me - memberId가 있으면 내 정보 조회 성공")
    void me_success() throws Exception {
        // given
        Long memberId = 1L;

        MyPageDto.MeResponse res = MyPageDto.MeResponse.builder()
                // TODO: MeResponse 필드에 맞게 채우기 (예: name, phoneNumber 등)
                .build();

        Mockito.when(memberService.getMe(memberId)).thenReturn(res);

        // when & then
        mockMvc.perform(get("/me")
                        .requestAttr("memberId", memberId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    @DisplayName("GET /me - memberId가 없으면 IllegalArgumentException 발생")
    void me_missingMemberId_throws() throws Exception {
        // 컨트롤러에서 직접 IllegalArgumentException 던짐
        mockMvc.perform(get("/me"))
                .andExpect(result -> {
                    if (result.getResolvedException() == null) {
                        throw new AssertionError("예외가 발생해야 하는데 발생하지 않았습니다.");
                    }
                    if (!(result.getResolvedException() instanceof IllegalArgumentException)) {
                        throw new AssertionError("IllegalArgumentException 이어야 합니다. actual="
                                + result.getResolvedException().getClass().getName());
                    }
                });
        // 상태코드는 전역 예외처리(@RestControllerAdvice) 구현에 따라 400/500 달라질 수 있어서 여기선 예외 타입만 검증
    }

    @Test
    @DisplayName("GET /me/budget - memberId가 있으면 예산 조회 성공")
    void budget_success() throws Exception {
        // given
        Long memberId = 1L;

        MyPageDto.BudgetResponse res = MyPageDto.BudgetResponse.builder()
                .budget(0)
                .build();

        Mockito.when(memberService.getBudget(memberId)).thenReturn(res);

        // when & then
        mockMvc.perform(get("/me/budget")
                        .requestAttr("memberId", memberId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.result.budget").value(0));
    }

    @Test
    @DisplayName("GET /me/budget - memberId 없으면 400 (MissingRequestAttributeException)")
    void budget_missingMemberId_400() throws Exception {
        mockMvc.perform(get("/me/budget"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /me/budget - memberId가 있으면 예산 수정 성공")
    void updateBudget_success() throws Exception {
        // given
        Long memberId = 1L;

        MyPageDto.UpdateBudgetRequest req = MyPageDto.UpdateBudgetRequest.builder()
                // TODO: 실제 필드명에 맞추기 (보통 budget)
                .budget(10000)
                .build();

        MyPageDto.BudgetResponse res = MyPageDto.BudgetResponse.builder()
                .budget(10000)
                .build();

        Mockito.when(memberService.updateBudget(eq(memberId), any(MyPageDto.UpdateBudgetRequest.class)))
                .thenReturn(res);

        // when & then
        mockMvc.perform(patch("/me/budget")
                        .requestAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.result.budget").value(10000));
    }

    @Test
    @DisplayName("PATCH /me/budget - request body validation 실패 시 400")
    void updateBudget_validationFail_400() throws Exception {
        Long memberId = 1L;

        // 예: budget이 @NotNull이면 null/누락 시 validation 에러
        String invalidJson = "{}";

        mockMvc.perform(patch("/me/budget")
                        .requestAttr("memberId", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /me/budget - memberId 없으면 400")
    void updateBudget_missingMemberId_400() throws Exception {
        String body = "{\"budget\":1000}";

        mockMvc.perform(patch("/me/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
