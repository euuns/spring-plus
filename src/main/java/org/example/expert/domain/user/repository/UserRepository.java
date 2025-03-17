package org.example.expert.domain.user.repository;

import org.example.expert.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query(value = "SELECT * FROM users WHERE MATCH(nickname) AGAINST (:nickname IN NATURAL LANGUAGE MODE)", nativeQuery = true)
    User findByNicknameOnFullTextIndexing(String nickname);

    User findByNickname(String nickname);
}
