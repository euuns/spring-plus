package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.example.expert.domain.comment.entity.QComment.comment;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;

@Repository
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
    public Page<TodoSearchResponse> searchTodo(String title, LocalDateTime from, LocalDateTime to, String nickname, Pageable pageable) {

        List<TodoSearchResponse> todos = jpaQueryFactory
                .select(Projections.constructor(TodoSearchResponse.class,
                        todo.title,
                        JPAExpressions.select(manager.count()).from(manager).where(manager.todo.id.eq(todo.id)),
                        JPAExpressions.select(comment.count()).from(comment).where(comment.todo.id.eq(todo.id))))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .where(
                        containTitle(title),
                        createdAtDateRange(from, to),
                        containNickname(nickname)
                )
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long size = jpaQueryFactory.select(todo.id.countDistinct())
                .from(todo)
                .leftJoin(todo.managers, manager)
                .where(
                        containTitle(title),
                        createdAtDateRange(from, to),
                        containNickname(nickname)
                )
                .fetchOne();

        return new PageImpl<>(todos, pageable, size);
    }

    private BooleanExpression containTitle(String title) {
        return StringUtils.isBlank(title) ? null : todo.title.contains(title);
    }

    private BooleanExpression createdAtDateRange(LocalDateTime from, LocalDateTime to) {
        return Objects.isNull(from) ? null : todo.createdAt.between(from, to);
    }

    private BooleanExpression containNickname(String nickname) {
        return StringUtils.isBlank(nickname) ? null : manager.user.nickname.contains(nickname);
    }

}
