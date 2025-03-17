package org.example.expert.domain.user.repository;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.user.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public void bulkInsert(List<User> users, int batchSize) {
        // batchSize 크기만큼 나눠서 insert
        batchInsert(users, batchSize);
    }

    private void batchInsert(List<User> users, int batchSize) {
        String sql = "INSERT INTO users (email, password, user_role, nickname) values (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, users, batchSize, (ps, user) -> {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getUserRole().name());
            ps.setString(4, user.getNickname());
        });
    }
}
