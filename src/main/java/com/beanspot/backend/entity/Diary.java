package com.beanspot.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Diary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate date; // 어느 날짜의 일기인지

    private String content; // 일기 내용

    @Enumerated(EnumType.STRING)
    private CharacterType characterType; // 캐릭터 타입 (BINI, PANI)

    @Enumerated(EnumType.STRING)
    private EmotionType emotionType; // 감정 타입 (HAPPY, NEUTRAL, ANGRY, CONFUSED, KISS, CRY)

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DiaryStatus status = DiaryStatus.ACTIVE; // 일기 상태 (ACTIVE, DELETED)

    private LocalDateTime createdAt; // 일기 생성일

    private LocalDateTime updatedAt; // 일기 수정일

    private LocalDateTime deletedAt; // 일기 삭제일 (soft delete)

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = DiaryStatus.ACTIVE;
        }
    }

    public void updateDiary(String content, CharacterType characterType, EmotionType emotionType) {
        this.content = content;
        this.characterType = characterType;
        this.emotionType = emotionType;
        this.updatedAt = LocalDateTime.now();
    }

    public void deleteDiary() {
        this.status = DiaryStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }
}
