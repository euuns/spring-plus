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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TodoDslRepositoryImplTest {

    private static final Logger log = LoggerFactory.getLogger(TodoDslRepositoryImplTest.class);
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

    private Todo todo1;
    private Todo todo2;
    private Todo todo3;

    private User user1;
    private User user2;

    private Comment comment1;
    private Comment comment2;

    private Pageable pageable = PageRequest.of(0, 5);


    @Transactional
    @BeforeEach
    void setup() {
        user1 = new User("email@example.com", "pass1234!", UserRole.ROLE_USER, "user1");
        user2 = new User("emails@example.com", "pass1234!", UserRole.ROLE_USER, "user2");
        userRepository.save(user1);
        userRepository.save(user2);

        todo1 = new Todo("공부하기", "contents", "맑음", user1);
        todo2 = new Todo("공부", "contents", "흐림", user2);
        todo3 = new Todo("운동하기", "contents", "맑음", user1);

        todoRepository.save(todo1);
        todoRepository.save(todo2);
        todoRepository.save(todo3);

        comment1 = new Comment("user1 todo1", user1, todo1);
        comment2 = new Comment("user2 todo1", user2, todo1);

        commentRepository.save(comment1);
        commentRepository.save(comment2);

        Manager manager = new Manager(user2, todo1);
        managerRepository.save(manager);
    }

    @Test
    public void findByIdWithUserTest() {
        Optional<Todo> getTodo = todoRepository.findByIdWithUser(1L);
        assertEquals(getTodo.get().getUser().getEmail(), user1.getEmail());
    }

    @Test
    public void findAllByTitleContainingTest() {
        Page<TodoSearchResponse> searchTitle = todoRepository.findAllByTitleContaining("공부", pageable);
        List<TodoSearchResponse> list = searchTitle.toList();

        assertEquals(2, list.size());
        assertEquals(1, list.get(0).getTotalManagers());
        assertEquals(0, list.get(0).getTotalComments());
        assertEquals(2, list.get(1).getTotalComments());
    }

    @Test
    public void findAllByManagersNicknameContainingTest() {
        Page<TodoSearchResponse> searchNickname = todoRepository.findAllByManagersNicknameContaining("nick", pageable);
        List<TodoSearchResponse> list = searchNickname.toList();

        assertEquals(list.get(0).getTitle(), "title");
    }

    @Test
    public void searchTodoTest_title_검색() {
        LocalDateTime start = LocalDate.MIN.atStartOfDay();
        LocalDateTime end = LocalDateTime.of(LocalDate.of(2025, 5, 5), LocalTime.MAX).withNano(0);

        Page<TodoSearchResponse> todos = todoRepository.searchTodo("공부", start, end, null, pageable);
        List<TodoSearchResponse> list = todos.toList();

        assertFalse(todo3.getTitle().contains("공부"));
        assertEquals(2, list.size());

        assertEquals(0, list.get(0).getTotalComments());
        assertEquals(1, list.get(0).getTotalManagers());

        assertEquals(2, list.get(1).getTotalComments());
        assertEquals(2, list.get(1).getTotalManagers());
    }

    @Test
    public void searchTodoTest_manager_nickname_검색() {
        LocalDateTime start = LocalDate.MIN.atStartOfDay();
        LocalDateTime end = LocalDateTime.of(LocalDate.of(2025, 5, 5), LocalTime.MAX).withNano(0);

        Page<TodoSearchResponse> todos = todoRepository.searchTodo(null, start, end, "user1", pageable);
        List<TodoSearchResponse> list = todos.toList();

        assertFalse(todo2.getManagers().contains("user1"));
        assertEquals(2, list.size());

        assertEquals(todo3.getTitle(), list.get(0).getTitle());
        assertEquals(0, list.get(0).getTotalComments());
        assertEquals(1, list.get(0).getTotalManagers());

        assertEquals(todo1.getTitle(), list.get(1).getTitle());
        assertEquals(2, list.get(1).getTotalComments());
        assertEquals(2, list.get(1).getTotalManagers());
    }
}