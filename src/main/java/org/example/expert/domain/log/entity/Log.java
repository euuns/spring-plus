package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "logs")
@EntityListeners(AuditingEntityListener.class)
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String log;

    private String result;

    @CreatedDate
    @Column
    private LocalDateTime timestamp;

    public Log() {
    }

    public Log(String log, String result) {
        this.log = log;
        this.result = result;
    }
}
