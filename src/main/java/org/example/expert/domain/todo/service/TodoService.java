package org.example.expert.domain.todo.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final WeatherClient weatherClient;

    public TodoSaveResponse saveTodo(AuthUser authUser, TodoSaveRequest todoSaveRequest) {
        User user = User.fromAuthUser(authUser);

        String weather = weatherClient.getTodayWeather();

        Todo newTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        Todo savedTodo = todoRepository.save(newTodo);

        return new TodoSaveResponse(
                savedTodo.getId(),
                savedTodo.getTitle(),
                savedTodo.getContents(),
                weather,
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    public Page<TodoResponse> getTodos(int page, int size, String weather, LocalDate startedAt, LocalDate endedAt) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Map<String, LocalDate> dateRange = getSearchDateRange(startedAt, endedAt);

        // 검색 기간 x
        if (dateRange.isEmpty()) {
            Page<Todo> todos = getTodosOnWeather(weather, pageable);
            return todos.map(todo -> new TodoResponse(
                    todo.getId(),
                    todo.getTitle(),
                    todo.getContents(),
                    todo.getWeather(),
                    new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                    todo.getCreatedAt(),
                    todo.getModifiedAt()
            ));
        }

        // 검색 기간 o
        Page<Todo> todos = getTodosOnWeatherDateRange(weather, dateRange, pageable);
        return todos.map(todo -> new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(todo.getUser().getId(), todo.getUser().getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        ));
    }


    public TodoResponse getTodo(long todoId) {
        Todo todo = todoRepository.findByIdWithUser(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        User user = todo.getUser();

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getContents(),
                todo.getWeather(),
                new UserResponse(user.getId(), user.getEmail()),
                todo.getCreatedAt(),
                todo.getModifiedAt()
        );
    }

//    public Page<TodoSearchResponse> searchTodo(int page, int size, String title, LocalDate startedAt, LocalDate endedAt, String manager) {
//        Pageable pageable = PageRequest.of(page - 1, size);
//        Map<String, LocalDate> dateRange = getSearchDateRange(startedAt, endedAt);
//
//        if (title != null) {
//            return todoRepository.findAllByTitleContaining(title, pageable);
//        }
//        if (manager != null) {
//            return todoRepository.findAllByManagersNicknameContaining(manager, pageable);
//        }
//        if (!dateRange.isEmpty()) {
//            LocalDateTime from = dateRange.get("startedAt").atStartOfDay();
//            LocalDateTime to = dateRange.get("endedAt").atStartOfDay();
//
//            return todoRepository.findAllByCreatedAtDateRange(from, to, pageable);
//        }
//
//        throw new InvalidRequestException("검색 조건을 입력해주세요.");
//    }


    public Page<TodoSearchResponse> searchTodo(int page, int size, String title, LocalDate startedAt, LocalDate endedAt, String manager) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Map<String, LocalDate> dateRange = getSearchDateRange(startedAt, endedAt);

        if (dateRange.isEmpty()) {
            return todoRepository.searchTodo(title, null, null, manager, pageable);
        }

        LocalDateTime start = dateRange.get("startedAt").atStartOfDay();
        LocalDateTime end = LocalDateTime.of(dateRange.get("endedAt"), LocalTime.MAX).withNano(0);

        return todoRepository.searchTodo(title, start, end, manager, pageable);
    }


    private Page<Todo> getTodosOnWeather(String weather, Pageable pageable) {
        if (weather == null) {
            // 검색 기간 x weather 조건 x
            return todoRepository.findAllByOrderByModifiedAtDesc(pageable);
        }
        // 검색 기간 x weather 조건 o
        return todoRepository.findAllByWeatherOrderByModifiedAt(weather, pageable);
    }

    private Page<Todo> getTodosOnWeatherDateRange(String weather, Map<String, LocalDate> range, Pageable pageable) {
        if (weather == null) {
            // 검색 기간 o weather 조건 x
            return todoRepository.findAllByModifiedAtDateRange(range.get("startedAt"), range.get("endedAt"), pageable);
        }
        // 검색 기간 o weather 조건 o
        return todoRepository.findTodosByWeatherAndDateRange(weather, range.get("startedAt"), range.get("endedAt"), pageable);
    }

    private Map<String, LocalDate> getSearchDateRange(LocalDate startedAt, LocalDate endedAt) {
        Map<String, LocalDate> period = new HashMap<>();

        // 시작 ~
        if (startedAt != null && endedAt == null) {
            period.put("startedAt", startedAt);
            period.put("endedAt", LocalDate.now());
        }

        // ~ 끝
        if (startedAt == null && endedAt != null) {
            period.put("startedAt", LocalDate.MIN);
            period.put("endedAt", endedAt);
        }

        // 시작 ~ 끝
        if (startedAt != null && endedAt != null) {
            period.put("startedAt", startedAt);
            period.put("endedAt", endedAt);
        }

        return period;
    }
}
