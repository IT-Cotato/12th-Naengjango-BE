package com.itcotato.naengjango.domain.notification.controller;

import com.itcotato.naengjango.domain.notification.dto.NotificationResponse;
import com.itcotato.naengjango.domain.notification.dto.UnreadCountResponse;
import com.itcotato.naengjango.domain.notification.exception.code.NotificationSuccessCode;
import com.itcotato.naengjango.domain.notification.service.CurrentMemberService;
import com.itcotato.naengjango.domain.notification.service.NotificationService;
import com.itcotato.naengjango.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "알림", description = "알림 관련 API")
@RequiredArgsConstructor
@RestController
@SecurityRequirement(name="BearerAuth")
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentMemberService currentMemberService;

    @Operation(
            summary = "내 알림 목록 조회 API by 이정환 (개발 완료)",
            description = """
			현재 로그인한 사용자의 알림 목록을 페이지 형태로 조회합니다.
			- `page`: 페이지 번호 (0부터 시작)
			- `size`: 한 페이지 알림 개수
			"""
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "NOTIFICATION200_1",
                    description = "내 알림 목록 조회 성공"
            )
    })
    @GetMapping
    public ApiResponse<Page<NotificationResponse>> getMyNotifications(
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(name = "size", description = "한 페이지 알림 개수", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        Long memberId = currentMemberService.getCurrentMemberId();
        Page<NotificationResponse> result = notificationService.getMyNotifications(memberId, page, size);
        return ApiResponse.onSuccess(NotificationSuccessCode.NOTIFICATION_LIST_FOUND, result);
    }

    @Operation(
            summary = "읽지 않은 알림 개수 조회 API by 이정환 (개발 완료)",
            description = "현재 로그인한 사용자의 읽지 않은 알림 개수를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "NOTIFICATION200_2",
                    description = "읽지 않은 알림 개수 조회 성공"
            )
    })
    @GetMapping("/unread-count")
    public ApiResponse<UnreadCountResponse> getUnreadCount() {
        Long memberId = currentMemberService.getCurrentMemberId();
        UnreadCountResponse result = new UnreadCountResponse(notificationService.getUnreadCount(memberId));
        return ApiResponse.onSuccess(NotificationSuccessCode.NOTIFICATION_UNREAD_COUNT_FOUND, result);
    }

    @Operation(
            summary = "알림 단건 읽음 처리 API by 이정환 (개발 완료)",
            description = """
			특정 알림을 읽음 처리합니다.
			- `id`: 읽음 처리할 알림 ID
			"""
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "NOTIFICATION200_3",
                    description = "알림 단건 읽음 처리 성공"
            )
    })
    @PatchMapping("/{id}/read")
    public ApiResponse<String> markRead(
            @Parameter(name = "id", description = "읽음 처리할 알림 ID", example = "10")
            @PathVariable Long id
    ) {
        Long memberId = currentMemberService.getCurrentMemberId();
        notificationService.markRead(memberId, id);
        return ApiResponse.onSuccess(NotificationSuccessCode.NOTIFICATION_MARK_READ_SUCCESS, "알림을 읽음 처리했습니다.");
    }

    @Operation(
            summary = "알림 전체 읽음 처리 API by 이정환 (개발 완료)",
            description = "현재 로그인한 사용자의 모든 알림을 읽음 처리합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "NOTIFICATION200_4",
                    description = "알림 전체 읽음 처리 성공"
            )
    })
    @PatchMapping("/read-all")
    public ApiResponse<String> markReadAll() {
        Long memberId = currentMemberService.getCurrentMemberId();
        notificationService.markReadAll(memberId);
        return ApiResponse.onSuccess(NotificationSuccessCode.NOTIFICATION_MARK_READ_ALL_SUCCESS, "모든 알림을 읽음 처리했습니다.");
    }
}
