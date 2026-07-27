package com.beanspot.backend.dto.user;

import com.beanspot.backend.entity.CharacterType;
import com.beanspot.backend.entity.EmotionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryRequestDto {

    @Schema(description = "일기 작성 날짜", example = "2026-01-30")
    private LocalDate date;

    @Schema(description = "캐릭터 타입 (GREEN: 푸웅, BROWN: 꾸웅)", example = "GREEN")
    private CharacterType characterType;

    @Schema(description = "감정 타입 (HAPPY, SAD, ANGRY 등)", example = "HAPPY")
    private EmotionType emotionType;

    @Schema(description = "일기 내용 (최대 200자)", example = "오늘은 스프링 부트 설정을 마쳤다. 뿌듯하다!")
    private String content;
}
