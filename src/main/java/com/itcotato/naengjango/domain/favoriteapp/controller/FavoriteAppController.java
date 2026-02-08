package com.itcotato.naengjango.domain.favoriteapp.controller;

import com.itcotato.naengjango.domain.favoriteapp.dto.FavoriteAppRequestDto;
import com.itcotato.naengjango.domain.favoriteapp.dto.FavoriteAppResponseDto;
import com.itcotato.naengjango.domain.favoriteapp.exception.code.FavoriteAppSuccessCode;
import com.itcotato.naengjango.domain.favoriteapp.service.FavoriteAppService;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite-apps")
@RequiredArgsConstructor
public class FavoriteAppController {

    private final FavoriteAppService favoriteAppService;

    /** 즐겨찾기 앱 추가 */
    @Operation(
            summary = "즐겨찾기 앱 추가 by 임준서 (개발 완료)",
            description = """
                            즐겨찾기 앱 추가 API 입니다.
                            - 요청 바디에 추가할 앱 정보(앱 이름)를 포함하여 전송합니다.
                            - 성공 시 추가 성공 메시지를 반환합니다.
                    """)
    @PostMapping
    public ApiResponse<Void> add(
            @AuthenticationPrincipal Member member,
            @RequestBody FavoriteAppRequestDto.FavoriteAppCreate request
    ) {
        favoriteAppService.add(member, request);
        return ApiResponse.onSuccess(FavoriteAppSuccessCode.FAVORITE_APP_ADD_SUCCESS, null);
    }

    /** 즐겨찾기 앱 조회 */
    @Operation(
            summary = "즐겨찾기 앱 조회 by 임준서 (개발 완료)",
            description = """
                            즐겨찾기 앱 조회 API 입니다.
                            - 로그인한 사용자의 즐겨찾기 앱 목록을 조회합니다.
                            - 성공 시 즐겨찾기 앱 리스트를 반환합니다.
                    """)
    @GetMapping
    public ApiResponse<List<FavoriteAppResponseDto.FavoriteAppResponse>> findMyApps(
            @AuthenticationPrincipal Member member
    ) {
        return ApiResponse.onSuccess(FavoriteAppSuccessCode.FAVORITE_APP_LIST_RETRIEVE_SUCCESS,
                favoriteAppService.findMyApps(member)
        );
    }

    /** 즐겨찾기 앱 삭제 */
    @Operation(
            summary = "즐겨찾기 앱 삭제 by 임준서 (개발 완료)",
            description = """
                            즐겨찾기 앱 삭제 API 입니다.
                            - 경로 변수로 삭제할 앱의 ID를 전달합니다.
                            - 성공 시 삭제 성공 메시지를 반환합니다.
                    """)
    @DeleteMapping("/{appId}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal Member member,
            @PathVariable Long appId
    ) {
        favoriteAppService.delete(member, appId);
        return ApiResponse.onSuccess(FavoriteAppSuccessCode.FAVORITE_APP_REMOVE_SUCCESS ,null);
    }
}
