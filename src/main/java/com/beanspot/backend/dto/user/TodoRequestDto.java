package com.beanspot.backend.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class TodoRequestDto {
    private String content; // 할 일 내용
    private LocalDate date; // 해당 날짜
    private Long announcementId; // 연결할 관심 공고 (없으면 일반 할 일)
}