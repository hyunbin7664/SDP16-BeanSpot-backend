package com.beanspot.backend.service;

import com.beanspot.backend.dto.user.TodoRequestDto;
import com.beanspot.backend.dto.user.TodoResponseDto;
import com.beanspot.backend.entity.Todo;
import com.beanspot.backend.entity.User;
import com.beanspot.backend.entity.announcement.Announcement;
import com.beanspot.backend.repository.TodoRepository;
import com.beanspot.backend.repository.UserRepository;
import com.beanspot.backend.repository.announcement.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final AnnouncementRepository announcementRepository;

    /**
     * 특정 날짜의 투두 리스트 조회
     */
    public List<TodoResponseDto> getDailyTodos(Long userId, LocalDate date) {
        // Repository에서 UserId와 날짜로 조회 (이름 주의: ByUserIdAndDate)
        return todoRepository.findAllByUserIdAndDate(userId, date)
                .stream()
                .map(TodoResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 투두 추가
     */
    @Transactional
    public Long addTodo(Long userId, TodoRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 공고에 속한 할 일이면 해당 공고를 연결 (없으면 일반 할 일)
        Announcement announcement = null;
        if (dto.getAnnouncementId() != null) {
            announcement = announcementRepository.findById(dto.getAnnouncementId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공고입니다."));
        }

        Todo todo = Todo.builder()
                .content(dto.getContent())
                .date(dto.getDate())
                .isCompleted(false) // 기본값은 미완료
                .user(user)
                .announcement(announcement)
                .build();

        return todoRepository.save(todo).getId();
    }

    /**
     * 투두 상태 변경 (완료/미완료 토글)
     */
    @Transactional
    public void toggleStatus(Long userId, Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 할 일이 없습니다."));

        // 본인 확인 보안 체크
        if (!todo.getUser().getId().equals(userId)) {
            throw new IllegalStateException("해당 기능에 대한 권한이 없습니다.");
        }

        todo.toggle(); // 엔티티의 토글 메서드 호출
    }

    /**
     * 투두 삭제
     */
    @Transactional
    public void deleteTodo(Long userId, Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 할 일이 없습니다."));

        if (!todo.getUser().getId().equals(userId)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        todoRepository.delete(todo);
    }
}