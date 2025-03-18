package org.example.expert.domain.todo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles("test")
@SpringBootTest
class TodoDslRepositoryImplTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ManagerRepository managerRepository;

    private Pageable pageable = PageRequest.of(0, 5);


    @Test
    public void searchTodoTest_title_검색() {
        User commentUser = new User("commentUser@example.com", "pass1234!", UserRole.ROLE_USER, "commentUser");
        userRepository.save(commentUser);

        Todo studyTodo = new Todo("공부하기", "contents", "맑음", commentUser);
        Todo exTodo = new Todo("운동하기", "contents", "맑음", commentUser);
        todoRepository.saveAll(List.of(studyTodo, exTodo));

        Comment comment1 = new Comment("user1 todo1", commentUser, studyTodo);
        commentRepository.save(comment1);

        LocalDateTime start = LocalDate.MIN.atStartOfDay();
        LocalDateTime end = LocalDateTime.of(LocalDate.of(2025, 5, 5), LocalTime.MAX).withNano(0);

        Page<TodoSearchResponse> todos = todoRepository.searchTodo("공부", start, end, null, pageable);
        List<TodoSearchResponse> list = todos.toList();

        assertFalse(exTodo.getTitle().contains("공부"));
        assertEquals(1, list.size());
        assertEquals(1, list.get(0).getTotalComments());
    }

    @Test
    public void searchTodoTest_manager_nickname_검색() {
        User todoUser = new User("todoUser@example.com", "pass1234!", UserRole.ROLE_USER, "todoUser");
        User manUser = new User("manUser@example.com", "pass1234!", UserRole.ROLE_USER, "manUser");
        userRepository.saveAll(List.of(todoUser, manUser));

        Todo todo1 = new Todo("글쓰기1", "contents", "맑음", todoUser);
        Todo todo2 = new Todo("글쓰기2", "contents", "맑음", manUser);
        todoRepository.saveAll(List.of(todo1, todo2));

        Manager manager = new Manager(todoUser, todo2);
        managerRepository.save(manager);

        LocalDateTime start = LocalDate.MIN.atStartOfDay();
        LocalDateTime end = LocalDateTime.of(LocalDate.of(2025, 5, 5), LocalTime.MAX).withNano(0);

        Page<TodoSearchResponse> todos = todoRepository.searchTodo(null, start, end, "manUser", pageable);
        List<TodoSearchResponse> list = todos.toList();

        assertFalse(todo1.getManagers().contains("manUser"));
        assertEquals(1, list.size());
    }
}