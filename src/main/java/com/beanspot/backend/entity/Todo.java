package com.beanspot.backend.entity;

import com.beanspot.backend.entity.announcement.Announcement;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder // 클래스 레벨로 이동하여 더 유연하게 사용합니다.
public class Todo extends BaseEntity { // 💡 BaseEntity 상속 추가

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 이 할 일이 속한 관심 공고. 공고와 무관한 일반 할 일이면 null 입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;

    private LocalDate date;    // 어느 날짜의 할 일인지

    private String content;    // 할 일 내용

    private boolean isCompleted; // 완료 여부

    /**
     * 💡 투두 상태 변경 (완료/미완료 토글)
     * 서비스 계층에서 호출하여 DB 데이터를 변경합니다.
     */
    public void toggle() {
        this.isCompleted = !this.isCompleted;
    }

    /**
     * 💡 투두 내용 수정
     * 나중에 할 일 내용을 고치고 싶을 때 사용합니다.
     */
    public void updateContent(String content) {
        this.content = content;
    }
}