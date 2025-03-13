package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TodoDslRepository {
    Optional<Todo> findByIdWithUser(Long todoId);
    Page<TodoSearchResponse> searchTodo(String title, LocalDateTime from, LocalDateTime to, String nickname, Pageable pageable);
    Page<TodoSearchResponse> findAllByTitleContaining(String title, Pageable pageable);
    Page<TodoSearchResponse> findAllByCreatedAtDateRange(LocalDateTime from, LocalDateTime to, Pageable pageable);
    Page<TodoSearchResponse> findAllByManagersNicknameContaining(String nickname, Pageable pageable);
}
