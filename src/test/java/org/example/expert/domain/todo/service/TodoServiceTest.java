package org.example.expert.domain.todo.service;

import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @InjectMocks
    private TodoService todoService;

    @Mock
    private TodoRepository todoRepository;

    Todo todo1;
    Todo todo2;
    Todo todo3;

    @BeforeEach
    void setUp() {
        todo1 = new Todo("운동하기", "내용", "맑음", new User());
        todo2 = new Todo("공부하기", "내용", "흐림", new User());
        todo3 = new Todo("공부", "내용", "맑음", new User());

        LocalDateTime modifiedAt1 = LocalDateTime.of(2024, 12, 31, 12, 12);
        LocalDateTime modifiedAt2 = LocalDateTime.of(2025, 2, 28, 12, 12);
        LocalDateTime modifiedAt3 = LocalDateTime.of(2025, 3, 31, 12, 12);

        todoRepository.save(todo1);
        todoRepository.save(todo2);
        todoRepository.save(todo3);

        ReflectionTestUtils.setField(todo1, "modifiedAt", modifiedAt1);
        ReflectionTestUtils.setField(todo2, "modifiedAt", modifiedAt2);
        ReflectionTestUtils.setField(todo3, "modifiedAt", modifiedAt3);
    }


    @Test
    void weather조건이_입력되지_않으면_일반_조회() {
        Page<Todo> weatherPage = new PageImpl<>(List.of(todo1, todo3));
        given(todoRepository.findAllByWeatherOrderByModifiedAt(eq("맑음"), any())).willReturn(weatherPage);

        Page<Todo> notWeatherPage = new PageImpl<>(List.of(todo1, todo2, todo3));
        given(todoRepository.findAllByOrderByModifiedAtDesc(any())).willReturn(notWeatherPage);

        Page<TodoResponse> weathers = todoService.getTodos(1, 5, "맑음", null, null);
        List<TodoResponse> weatherList = weathers.get().toList();

        Page<TodoResponse> notWeathers = todoService.getTodos(1, 5, null, null, null);
        List<TodoResponse> notWeatherList = notWeathers.get().toList();

        assertEquals(2, weatherList.size());
        assertEquals(3, notWeatherList.size());
        assertEquals("맑음", weatherList.get(0).getWeather());
    }

    @Test
    void endedAt이_입력되지_않으면_now가_end조건() {
        LocalDate startedAt = LocalDate.of(2025, 2, 1);

        Page<Todo> page = new PageImpl<>(List.of(todo2));
        given(todoRepository.findAllByModifiedAtDateRange(eq(startedAt), eq(LocalDate.now()), any())).willReturn(page);

        Page<TodoResponse> pageList = todoService.getTodos(1, 5, null, startedAt, null);
        List<TodoResponse> todoList = pageList.get().toList();

        assertEquals(1, todoList.size());
        assertEquals(todo2.getTitle(), todoList.get(0).getTitle());
        assertTrue(todoList.get(0).getModifiedAt().isAfter(startedAt.atStartOfDay()));
    }

    @Test
    void startedAt이_입력되지_않으면_과거의_모든_글_조회() {
        LocalDate startedAt = LocalDate.of(2025, 3, 1);
        LocalDate endedAt = LocalDate.of(2025, 3, 8);

        Page<Todo> page1 = new PageImpl<>(List.of(todo1, todo2));
        given(todoRepository.findAllByModifiedAtDateRange(eq(LocalDate.MIN), eq(endedAt), any())).willReturn(page1);

        Page<Todo> page2 = new PageImpl<>(List.of(todo1, todo2, todo3));
        given(todoRepository.findAllByModifiedAtDateRange(eq(startedAt), eq(endedAt), any())).willReturn(page2);

        Page<TodoResponse> notStartedAtPage = todoService.getTodos(1, 5, null, null, endedAt);
        List<TodoResponse> todoList1 = notStartedAtPage.get().toList();
        Page<TodoResponse> startedAtPage = todoService.getTodos(1, 5, null, startedAt, endedAt);
        List<TodoResponse> todoList2 = startedAtPage.get().toList();

        assertEquals(2, todoList1.size());
        assertNotEquals(todoList1.get(0).getModifiedAt(), todoList1.get(1).getModifiedAt());
        assertTrue(todoList1.get(0).getModifiedAt().isBefore(endedAt.atStartOfDay()));

        assertEquals(3, todoList2.size());

    }

}