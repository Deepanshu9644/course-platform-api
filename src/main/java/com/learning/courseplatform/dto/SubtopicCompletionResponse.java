package com.learning.courseplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubtopicCompletionResponse {
    private String subtopicId;
    private Boolean completed;
    private LocalDateTime completedAt;
}
