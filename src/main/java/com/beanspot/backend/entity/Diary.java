package com.beanspot.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private CharacterType characterType; // GREEN(푸웅), BROWN(꾸웅)

    @Enumerated(EnumType.STRING)
    private EmotionType emotionType;     // HAPPY, ANGRY, SAD, SURPRISED, CALM, TIRED

    @Column(length = 200)
    private String content; // 최대 200자

    @Builder
    public Diary(User user, LocalDate date, CharacterType characterType, EmotionType emotionType, String content) {
        this.user = user;
        this.date = date;
        this.characterType = characterType;
        this.emotionType = emotionType;
        this.content = content;
    }

    public void update(LocalDate date, CharacterType characterType, EmotionType emotionType, String content) {
        this.date = date;
        this.characterType = characterType;
        this.emotionType = emotionType;
        this.content = content;
    }
}
