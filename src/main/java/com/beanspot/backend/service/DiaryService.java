package com.beanspot.backend.service;

import com.beanspot.backend.dto.user.DiaryRequestDto;
import com.beanspot.backend.dto.user.DiaryResponseDto;
import com.beanspot.backend.entity.Diary;
import com.beanspot.backend.entity.User;
import com.beanspot.backend.repository.DiaryRepository;
import com.beanspot.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    /**
     * 새로운 일기 작성 (하루에 한 건만 허용)
     */
    @Transactional
    public Long saveDiary(Long userId, DiaryRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        diaryRepository.findByUserIdAndDate(userId, dto.getDate())
                .ifPresent(d -> {
                    throw new IllegalStateException("해당 날짜에 이미 작성된 일기가 있습니다.");
                });

        Diary diary = Diary.builder()
                .user(user)
                .date(dto.getDate())
                .characterType(dto.getCharacterType())
                .emotionType(dto.getEmotionType())
                .content(dto.getContent())
                .build();

        return diaryRepository.save(diary).getId();
    }

    /**
     * 특정 연/월의 일기 목록 조회 (캘린더 표시용)
     */
    public List<DiaryResponseDto> getMonthlyDiaries(Long userId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return diaryRepository.findAllByUserIdAndDateBetween(userId, startDate, endDate)
                .stream()
                .map(DiaryResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 일기 상세 조회
     */
    public DiaryResponseDto getDiaryDetail(Long userId, Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일기가 없습니다."));

        if (!diary.getUser().getId().equals(userId)) {
            throw new IllegalStateException("해당 기능에 대한 권한이 없습니다.");
        }

        return DiaryResponseDto.from(diary);
    }

    /**
     * 일기 수정
     */
    @Transactional
    public void updateDiary(Long userId, Long diaryId, DiaryRequestDto dto) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일기가 없습니다."));

        if (!diary.getUser().getId().equals(userId)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        diary.update(dto.getDate(), dto.getCharacterType(), dto.getEmotionType(), dto.getContent());
    }

    /**
     * 일기 삭제
     */
    @Transactional
    public void deleteDiary(Long userId, Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일기가 없습니다."));

        if (!diary.getUser().getId().equals(userId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        diaryRepository.delete(diary);
    }
}
