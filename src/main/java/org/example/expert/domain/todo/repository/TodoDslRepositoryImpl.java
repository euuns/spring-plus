package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@RequiredArgsConstructor
public class TodoDslRepositoryImpl implements TodoDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        Todo findTodo = jpaQueryFactory.select(todo)
                .from(todo)
                .leftJoin(todo.user, user)
                .fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(findTodo);
    }

    @Override
    public Page<TodoSearchResponse> findAllByTitleContaining(String title, Pageable pageable) {
        List<TodoSearchResponse> todos = jpaQueryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.title,
                        JPAExpressions.select(manager.count()).from(manager).where(manager.todo.id.eq(todo.id)),
                        JPAExpressions.select(comment.count()).from(comment).where(comment.todo.id.eq(todo.id))))
                .from(todo)
                .where(todo.title.contains(title))
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long size = jpaQueryFactory.select(todo.count())
                .from(todo)
                .where(todo.title.contains(title))
                .fetchOne();

        return new PageImpl<>(todos, pageable, size);
    }

    @Override
    public Page<TodoSearchResponse> findAllByCreatedAtDateRange(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        List<TodoSearchResponse> todos = jpaQueryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.title,
                        JPAExpressions.select(manager.count()).from(manager).where(manager.todo.id.eq(todo.id)),
                        JPAExpressions.select(comment.count()).from(comment).where(comment.todo.id.eq(todo.id))))
                .from(todo)
                .where(todo.createdAt.between(from, to))
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long size = jpaQueryFactory.select(todo.count())
                .from(todo)
                .where(todo.createdAt.between(from, to))
                .fetchOne();

        return new PageImpl<>(todos, pageable, size);
    }

    @Override
    public Page<TodoSearchResponse> findAllByManagersNicknameContaining(String nickname, Pageable pageable) {
        List<TodoSearchResponse> todos = jpaQueryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.title,
                        JPAExpressions.select(manager.count()).from(manager).where(manager.todo.id.eq(todo.id)),
                        JPAExpressions.select(comment.count()).from(comment).where(comment.todo.id.eq(todo.id))))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .where(manager.user.nickname.contains(nickname))
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long size = jpaQueryFactory.select(todo.count())
                .from(todo)
                .leftJoin(todo.managers, manager)
                .where(manager.user.nickname.contains(nickname))
                .fetchOne();

        return new PageImpl<>(todos, pageable, size);
    }
}
