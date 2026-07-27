package com.beanspot.backend.dto.user;

import com.beanspot.backend.entity.CharacterType;
import com.beanspot.backend.entity.Diary;
import com.beanspot.backend.entity.EmotionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryResponseDto {

    @Schema(description = "일기 ID (고유 번호)", example = "1")
    private Long id;

    @Schema(description = "일기 작성 날짜", example = "2026-01-30")
    private LocalDate date;

    @Schema(description = "캐릭터 타입", example = "GREEN")
    private CharacterType characterType;

    @Schema(description = "감정 타입", example = "HAPPY")
    private EmotionType emotionType;

    @Schema(description = "일기 내용", example = "오늘은 스프링 부트 기능을 완성했다!")
    private String content;

    public static DiaryResponseDto from(Diary diary) {
        return DiaryResponseDto.builder()
                .id(diary.getId())
                .date(diary.getDate())
                .characterType(diary.getCharacterType())
                .emotionType(diary.getEmotionType())
                .content(diary.getContent())
                .build();
    }
}
