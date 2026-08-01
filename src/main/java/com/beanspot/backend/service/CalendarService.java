package com.beanspot.backend.service;

import com.beanspot.backend.dto.user.CalendarScheduleResponseDto;
import com.beanspot.backend.entity.User;
import com.beanspot.backend.entity.announcement.Announcement;
import com.beanspot.backend.entity.announcement.AnnouncementFavorite;
import com.beanspot.backend.repository.UserRepository;
import com.beanspot.backend.repository.announcement.AnnouncementFavoriteRepository;
import com.beanspot.backend.repository.announcement.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 캘린더에 표시할 관심 공고 일정 및 관심 공고 등록/해제.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    private final AnnouncementFavoriteRepository favoriteRepository;
    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    /**
     * 특정 연/월에 활동기간이 걸쳐 있는 관심 공고 목록.
     * 달 경계를 넘나드는 공고도 포함되도록 "구간 겹침"으로 조회합니다.
     */
    public List<CalendarScheduleResponseDto> getMonthlySchedules(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate rangeStart = yearMonth.atDay(1);
        LocalDate rangeEnd = yearMonth.atEndOfMonth();

        return favoriteRepository.findOverlappingRange(userId, rangeStart, rangeEnd)
                .stream()
                .map(AnnouncementFavorite::getAnnouncement)
                .map(CalendarScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    /** 내 관심 공고 전체 목록 */
    public List<CalendarScheduleResponseDto> getFavorites(Long userId) {
        return favoriteRepository.findAllByUserId(userId)
                .stream()
                .map(AnnouncementFavorite::getAnnouncement)
                .map(CalendarScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    /** 관심 공고 등록. 이미 등록돼 있으면 아무 것도 하지 않습니다(멱등). */
    @Transactional
    public void addFavorite(Long userId, Long announcementId) {
        if (favoriteRepository.existsByUserIdAndAnnouncementId(userId, announcementId)) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다."));

        favoriteRepository.save(
                AnnouncementFavorite.builder()
                        .user(user)
                        .announcement(announcement)
                        .build());
    }

    /** 관심 공고 해제. 등록돼 있지 않아도 오류로 보지 않습니다(멱등). */
    @Transactional
    public void removeFavorite(Long userId, Long announcementId) {
        favoriteRepository.findByUserIdAndAnnouncementId(userId, announcementId)
                .ifPresent(favoriteRepository::delete);
    }

    public boolean isFavorite(Long userId, Long announcementId) {
        return favoriteRepository.existsByUserIdAndAnnouncementId(userId, announcementId);
    }
}
