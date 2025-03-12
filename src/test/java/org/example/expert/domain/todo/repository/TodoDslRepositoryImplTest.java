package org.example.expert.domain.todo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TodoDslRepositoryImplTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    private Todo todo;
    private User user;

    @BeforeEach
    void setup() {
        user = new User("email@email.com", "pass1234!", UserRole.ROLE_USER, "nickname");
        User saveUser = userRepository.save(user);
        todo = new Todo("title", "contents", "weather", saveUser);
        todoRepository.save(todo);
    }

    @Test
    public void findByIdWithUserTest() {
        Optional<Todo> getTodo = todoRepository.findByIdWithUser(1L);
        assertEquals(getTodo.get().getUser().getEmail(), user.getEmail());
    }
}