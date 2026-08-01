package com.beanspot.backend.dto.user;

import com.beanspot.backend.entity.announcement.Announcement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 캘린더에 표시할 관심 공고 일정.
 * 활동기간(startDate~endDate)을 그대로 내려주고, 어느 날짜에 칠할지는 클라이언트가 계산합니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarScheduleResponseDto {

    @Schema(description = "공고 ID", example = "1")
    private Long announcementId;

    @Schema(description = "공고 제목", example = "줍깅 캠페인")
    private String title;

    @Schema(description = "활동 시작일", example = "2025-12-04")
    private LocalDate startDate;

    @Schema(description = "활동 종료일", example = "2025-12-10")
    private LocalDate endDate;

    public static CalendarScheduleResponseDto from(Announcement announcement) {
        return CalendarScheduleResponseDto.builder()
                .announcementId(announcement.getId())
                .title(announcement.getTitle())
                .startDate(announcement.getStartDate())
                .endDate(announcement.getEndDate())
                .build();
    }
}
