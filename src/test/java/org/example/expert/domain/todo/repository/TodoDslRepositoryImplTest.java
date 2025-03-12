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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

    private Todo todo;
    private User user;
    private Comment comment1;
    private Comment comment2;

    private Pageable pageable = PageRequest.of(0, 5);

    @Transactional
    @BeforeEach
    void setup() {
        user = new User("email@example.com", "pass1234!", UserRole.ROLE_USER, "nickname");
        User saveUser = userRepository.save(user);

        todo = new Todo("title", "contents", "weather", saveUser);
        Todo saveTodo = todoRepository.save(todo);

        ReflectionTestUtils.setField(saveTodo, "createdAt", LocalDateTime.of(2025,1,4,14,46));

        comment1 = new Comment("1", saveUser, saveTodo);
        comment2 = new Comment("2", saveUser, saveTodo);

        commentRepository.save(comment1);
        commentRepository.save(comment2);
    }

    @Test
    public void findByIdWithUserTest() {
        Optional<Todo> getTodo = todoRepository.findByIdWithUser(1L);
        assertEquals(getTodo.get().getUser().getEmail(), user.getEmail());
    }

    @Test
    public void findAllByTitleContainingTest() {
        Page<TodoSearchResponse> searchTitle = todoRepository.findAllByTitleContaining("ti", pageable);
        List<TodoSearchResponse> list = searchTitle.toList();

        assertEquals(list.get(0).getTotalManagers(), 1);
        assertEquals(list.get(0).getTotalComments(), 2);
    }

    @Test
    public void findAllByCreatedAtDateRangeTest(){
        LocalDateTime from = LocalDateTime.of(2024,12,25,12,30);
        LocalDateTime to = LocalDateTime.now();

        Page<TodoSearchResponse> searchDate = todoRepository.findAllByCreatedAtDateRange(from, to, pageable);
        List<TodoSearchResponse> list = searchDate.toList();

        assertNotNull(list);
    }

    @Test
    public void findAllByManagersNicknameContainingTest(){
        Page<TodoSearchResponse> searchNickname = todoRepository.findAllByManagersNicknameContaining("nick", pageable);
        List<TodoSearchResponse> list = searchNickname.toList();

        assertEquals(list.get(0).getTitle(), "title");
    }
}