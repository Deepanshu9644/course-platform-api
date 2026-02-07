package com.learning.courseplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subtopics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subtopic {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    @JsonIgnore
    private Topic topic;

    @OneToMany(mappedBy = "subtopic", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<SubtopicProgress> progress = new HashSet<>();
}
