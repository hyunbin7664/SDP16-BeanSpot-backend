package com.beanspot.backend.entity.announcement;

import com.beanspot.backend.entity.BaseEntity;
import com.beanspot.backend.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * 관심 공고. 사용자가 등록해둔 공고를 캘린더에서 활동기간으로 표시하는 데 사용합니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "announcement_favorite",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_favorite_user_announcement",
                columnNames = {"user_id", "announcement_id"}
        )
)
public class AnnouncementFavorite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    @Builder
    public AnnouncementFavorite(User user, Announcement announcement) {
        this.user = user;
        this.announcement = announcement;
    }
}
