package com.beanspot.backend.controller;

import com.beanspot.backend.dto.user.DiaryRequestDto;
import com.beanspot.backend.dto.user.DiaryResponseDto;
import com.beanspot.backend.security.CurrentUser;
import com.beanspot.backend.security.UserPrincipal;
import com.beanspot.backend.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Diary", description = "일기 관련 API")
@RestController
@RequestMapping("/api/v1/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @Operation(summary = "일기 작성", description = "새로운 일기를 작성합니다.")
    @PostMapping
    public ResponseEntity<Long> create(@CurrentUser UserPrincipal userPrincipal,
                                       @RequestBody DiaryRequestDto dto) {
        Long diaryId = diaryService.saveDiary(userPrincipal.getId(), dto);
        return ResponseEntity.ok(diaryId);
    }

    @Operation(summary = "월별 일기 목록 조회", description = "특정 연도와 월의 일기 목록을 가져옵니다.")
    @GetMapping
    public ResponseEntity<List<DiaryResponseDto>> getMonthlyDiaries(
            @CurrentUser UserPrincipal userPrincipal,
            @RequestParam int year,
            @RequestParam int month) {
        List<DiaryResponseDto> diaries = diaryService.getMonthlyDiaries(userPrincipal.getId(), year, month);
        return ResponseEntity.ok(diaries);
    }

    @Operation(summary = "일기 상세 조회", description = "특정 ID의 일기 상세 내용을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<DiaryResponseDto> getDetail(
            @CurrentUser UserPrincipal userPrincipal,
            @PathVariable Long id) {
        DiaryResponseDto diary = diaryService.getDiaryDetail(userPrincipal.getId(), id);
        return ResponseEntity.ok(diary);
    }

    @Operation(summary = "일기 수정", description = "기존에 작성한 일기를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@CurrentUser UserPrincipal userPrincipal,
                                       @PathVariable Long id,
                                       @RequestBody DiaryRequestDto dto) {
        diaryService.updateDiary(userPrincipal.getId(), id, dto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "일기 삭제", description = "일기를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@CurrentUser UserPrincipal userPrincipal,
                                       @PathVariable Long id) {
        diaryService.deleteDiary(userPrincipal.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
