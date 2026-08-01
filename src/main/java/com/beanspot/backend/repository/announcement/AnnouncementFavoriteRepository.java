package com.beanspot.backend.repository.announcement;

import com.beanspot.backend.entity.announcement.AnnouncementFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AnnouncementFavoriteRepository extends JpaRepository<AnnouncementFavorite, Long> {

    Optional<AnnouncementFavorite> findByUserIdAndAnnouncementId(Long userId, Long announcementId);

    boolean existsByUserIdAndAnnouncementId(Long userId, Long announcementId);

    /** 내 관심 공고 목록 (최근 등록 순) */
    @Query("select f from AnnouncementFavorite f join fetch f.announcement "
            + "where f.user.id = :userId order by f.createdAt desc")
    List<AnnouncementFavorite> findAllByUserId(@Param("userId") Long userId);

    /**
     * 활동기간이 주어진 구간과 겹치는 관심 공고 조회 (캘린더 표시용).
     * 공고 시작일 <= 구간 끝  AND  공고 종료일 >= 구간 시작 이면 겹침.
     */
    @Query("select f from AnnouncementFavorite f join fetch f.announcement a "
            + "where f.user.id = :userId "
            + "and a.startDate <= :rangeEnd and a.endDate >= :rangeStart "
            + "order by a.startDate asc")
    List<AnnouncementFavorite> findOverlappingRange(
            @Param("userId") Long userId,
            @Param("rangeStart") LocalDate rangeStart,
            @Param("rangeEnd") LocalDate rangeEnd);
}
