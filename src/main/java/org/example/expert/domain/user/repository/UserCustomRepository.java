package org.example.expert.domain.user.repository;

import org.example.expert.domain.user.entity.User;

import java.util.List;

public interface UserCustomRepository {

    void bulkInsert(List<User> users, int batchSize);
}
