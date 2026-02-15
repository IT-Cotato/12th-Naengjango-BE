package com.itcotato.naengjango.domain.igloo.controller;

import com.itcotato.naengjango.domain.igloo.exception.code.IglooSuccessCode;
import com.itcotato.naengjango.domain.igloo.service.IglooService;
import com.itcotato.naengjango.domain.igloo.dto.IglooResponseDto;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/igloo")
public class IglooController {

    private final IglooService iglooService;

    /**
     * 이글루 상태 조회
     */
    @Operation(
            summary = "이글루 상태 조회 by 임준서 (개발 완료)",
            description = """
                    이글루 상태 조회 API 입니다.
                    - 현재 이글루의 단계와 다음 단계까지 필요한 눈덩이 개수를 반환합니다.
                    """)
    @GetMapping("/status")
    public ApiResponse<IglooResponseDto.Status> status(@AuthenticationPrincipal Member member) {
        return ApiResponse.onSuccess(IglooSuccessCode.IGLOO_STATUS_SUCCESS , iglooService.getStatus(member));
    }

    /**
     * 수동 업그레이드
     * - 필요 눈덩이만큼 소비
     * - 단계 +1
     */
    @Operation(
            summary = "이글루 업그레이드 by 임준서 (개발 완료)",
            description = """
                    이글루 업그레이드 API 입니다.
                    - 이글루 업그레이드에 필요한 눈덩이를 소비하고, 이글루 단계를 상승시킵니다.
                    - 업그레이드 후의 이글루 단계와 남은 눈덩이 개수를 반환합니다.
                    """)
    @PostMapping("/upgrade")
    public ApiResponse<IglooResponseDto.UpgradeResult> upgrade(@AuthenticationPrincipal Member member) {
        return ApiResponse.onSuccess(IglooSuccessCode.IGLOO_UPGRADE_SUCCESS, iglooService.upgrade(member));
    }

    /**
     * 이글루 단계 하락 (프론트 confirm 이후 호출)
     */
    @Operation(
            summary = "이글루 다운그레이드 by 임준서 (개발 완료)",
            description = """
                    이글루 다운그레이드 API 입니다.
                    - 이글루 단계 하락에 필요한 눈덩이를 소비하고, 이글루 단계를 하락시킵니다.
                    - 하락 후의 이글루 단계와 남은 눈덩이 개수를 반환합니다.
                    """)
    @PostMapping("/downgrade")
    public ApiResponse<IglooResponseDto.Status> downgrade(
            @AuthenticationPrincipal Member member
    ) {
        return ApiResponse.onSuccess(
                IglooSuccessCode.IGLOO_DOWNGRADE_SUCCESS,
                iglooService.downgrade(member)
        );
    }
}
