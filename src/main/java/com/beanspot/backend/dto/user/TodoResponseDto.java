package com.beanspot.backend.dto.user;

import com.beanspot.backend.entity.Todo; // 💡 Todo 엔티티를 불러옵니다.
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate; // 💡 LocalDate 도구를 불러옵니다.

@Getter
@Builder
@NoArgsConstructor // 💡 기본 생성자도 추가하는 것이 안전합니다.
@AllArgsConstructor
public class TodoResponseDto {
    private Long id;
    private String content;
    private boolean isCompleted;
    private LocalDate date;
    private Long announcementId; // 연결된 관심 공고 (없으면 null)

    public static TodoResponseDto from(Todo todo) {
        return TodoResponseDto.builder()
                .id(todo.getId())
                .content(todo.getContent())
                .isCompleted(todo.isCompleted())
                .date(todo.getDate())
                .announcementId(
                        todo.getAnnouncement() != null ? todo.getAnnouncement().getId() : null)
                .build();
    }
}