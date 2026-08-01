package com.beanspot.backend.controller;

import com.beanspot.backend.dto.user.CalendarScheduleResponseDto;
import com.beanspot.backend.security.CurrentUser;
import com.beanspot.backend.security.UserPrincipal;
import com.beanspot.backend.service.CalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Calendar", description = "캘린더(관심 공고 일정) 관련 API")
@RestController
@RequestMapping("/api/v1/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @Operation(summary = "월별 일정 조회",
            description = "해당 연/월에 활동기간이 걸쳐 있는 관심 공고 목록을 반환합니다.")
    @GetMapping("/schedules")
    public ResponseEntity<List<CalendarScheduleResponseDto>> getMonthlySchedules(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(
                calendarService.getMonthlySchedules(userPrincipal.getId(), year, month));
    }

    @Operation(summary = "내 관심 공고 목록", description = "등록한 관심 공고를 최근 등록 순으로 반환합니다.")
    @GetMapping("/favorites")
    public ResponseEntity<List<CalendarScheduleResponseDto>> getFavorites(
            @CurrentUser UserPrincipal userPrincipal) {
        return ResponseEntity.ok(calendarService.getFavorites(userPrincipal.getId()));
    }

    @Operation(summary = "관심 공고 등록", description = "이미 등록돼 있으면 아무 동작도 하지 않습니다.")
    @PostMapping("/favorites/{announcementId}")
    public ResponseEntity<Void> addFavorite(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long announcementId) {
        calendarService.addFavorite(userPrincipal.getId(), announcementId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "관심 공고 해제", description = "등록돼 있지 않아도 오류로 보지 않습니다.")
    @DeleteMapping("/favorites/{announcementId}")
    public ResponseEntity<Void> removeFavorite(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long announcementId) {
        calendarService.removeFavorite(userPrincipal.getId(), announcementId);
        return ResponseEntity.noContent().build();
    }
}
