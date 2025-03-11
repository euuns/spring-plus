package org.example.expert.domain.todo.service;

import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Spy
    @InjectMocks
    private TodoService todoService;

    @Mock
    private TodoRepository todoRepository;


    @Test
    void weather조건이_입력되지_않으면_일반_조회() {
        Todo weatherTodo = new Todo("타이틀", "내용", "테스트", new User());
        Todo notWeatherTodo = new Todo("타이틀", "내용", "조건 불일치", new User());

        Page<Todo> weatherPage = new PageImpl<>(List.of(weatherTodo));
        given(todoRepository.findAllByWeatherOrderByModifiedAt(eq("테스트"), any())).willReturn(weatherPage);

        Page<Todo> notWeatherPage = new PageImpl<>(List.of(weatherTodo, notWeatherTodo));
        given(todoRepository.findAllByOrderByModifiedAtDesc(any())).willReturn(notWeatherPage);

        Page<TodoResponse> weathers = todoService.getTodos(1, 5, "테스트", null, null);
        List<TodoResponse> weatherList = weathers.get().toList();

        Page<TodoResponse> notWeathers = todoService.getTodos(1, 5, null, null, null);
        List<TodoResponse> notWeatherList = notWeathers.get().toList();

        assertEquals(1, weatherList.size());
        assertEquals(2, notWeatherList.size());
        assertEquals("테스트", weatherList.get(0).getWeather());
    }

    @Test
    void endedAt이_입력되지_않으면_now가_end조건() {
        Todo todo1 = new Todo("타이틀", "내용", "테스트", new User());
        Todo todo2 = new Todo("타이틀", "내용", "테스트", new User());

        LocalDate startedAt = LocalDate.of(2025, 3, 1);
        LocalDateTime modifiedAt1 = LocalDateTime.of(2025, 3, 5, 12, 12);
        LocalDateTime modifiedAt2 = LocalDateTime.of(2025, 3, 15, 12, 12);

        ReflectionTestUtils.setField(todo1, "modifiedAt", modifiedAt1);
        ReflectionTestUtils.setField(todo2, "modifiedAt", modifiedAt2);

        Page<Todo> page = new PageImpl<>(List.of(todo1));
        given(todoRepository.findAllByDateRange(eq(startedAt), eq(LocalDate.now()), any())).willReturn(page);

        Page<TodoResponse> pageList = todoService.getTodos(1, 5, null, startedAt, null);
        List<TodoResponse> todoList = pageList.get().toList();

        assertEquals(1, todoList.size());
        assertTrue(todoList.get(0).getModifiedAt().isAfter(startedAt.atStartOfDay()));
    }

    @Test
    void startedAt이_입력되지_않으면_과거의_모든_글_조회() {
        Todo todo1 = new Todo("타이틀", "내용", "테스트", new User());
        Todo todo2 = new Todo("타이틀", "내용", "테스트", new User());
        Todo todo3 = new Todo("타이틀", "내용", "테스트", new User());

        LocalDate startedAt = LocalDate.of(2025, 3, 1);
        LocalDate endedAt = LocalDate.of(2025, 3, 8);
        LocalDateTime modifiedAt1 = LocalDateTime.of(2025, 3, 1, 12, 12);
        LocalDateTime modifiedAt2 = LocalDateTime.of(2025, 3, 4, 12, 12);
        LocalDateTime modifiedAt3 = LocalDateTime.of(2025, 3, 10, 12, 12);

        ReflectionTestUtils.setField(todo1, "modifiedAt", modifiedAt1);
        ReflectionTestUtils.setField(todo2, "modifiedAt", modifiedAt2);
        ReflectionTestUtils.setField(todo3, "modifiedAt", modifiedAt3);

        Page<Todo> page1 = new PageImpl<>(List.of(todo1, todo2));
        given(todoRepository.findAllByDateRange(eq(LocalDate.MIN), eq(endedAt), any())).willReturn(page1);

        Page<Todo> page2 = new PageImpl<>(List.of(todo1, todo2, todo3));
        given(todoRepository.findAllByDateRange(eq(startedAt), eq(endedAt), any())).willReturn(page2);

        Page<TodoResponse> notStartedAtPage = todoService.getTodos(1, 5, null, null, endedAt);
        List<TodoResponse> todoList1 = notStartedAtPage.get().toList();
        Page<TodoResponse> startedAtPage = todoService.getTodos(1, 5, null, startedAt, endedAt);
        List<TodoResponse> todoList2 = startedAtPage.get().toList();

        assertEquals(2, todoList1.size());
        assertEquals(modifiedAt1, todoList1.get(0).getModifiedAt());
        assertEquals(modifiedAt2, todoList1.get(1).getModifiedAt());
        assertNotEquals(todoList1.get(0).getModifiedAt(), todoList1.get(1).getModifiedAt());
        assertTrue(todoList1.get(0).getModifiedAt().isBefore(endedAt.atStartOfDay()));

        assertEquals(3, todoList2.size());

    }

}