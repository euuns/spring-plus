package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface TodoRepository extends JpaRepository<Todo, Long>, TodoDslRepository {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u WHERE t.weather = :weather ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByWeatherOrderByModifiedAt(String weather, Pageable pageable);

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u " +
            "WHERE t.modifiedAt BETWEEN :startedAt AND :endedAt ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByDateRange(LocalDate startedAt, LocalDate endedAt, Pageable pageable);

    @Query("SELECT t FROM Todo t " +
            "LEFT JOIN FETCH t.user u " +
            "WHERE t.weather = :weather " +
            "AND t.modifiedAt BETWEEN :startedAt AND :endedAt " +
            "ORDER BY t.modifiedAt DESC")
    Page<Todo> findTodosByWeatherAndDateRange(String weather, LocalDate startedAt, LocalDate endedAt, Pageable pageable);
}
