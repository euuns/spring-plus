package org.example.expert.domain.todo.dto.response;

import lombok.Getter;

@Getter
public class TodoSearchResponse {
    String title;
    Long totalManagers;
    Long totalComments;

    public TodoSearchResponse(String title, Long totalManagers, Long totalComments) {
        this.title = title;
        this.totalManagers = totalManagers;
        this.totalComments = totalComments;
    }
}
