package com.learning.courseplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "subtopic_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "subtopic_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubtopicProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subtopic_id", nullable = false)
    @JsonIgnore
    private Subtopic subtopic;

    @Column(nullable = false)
    private Boolean completed = false;

    @Column
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (completed && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (completed && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }
}
