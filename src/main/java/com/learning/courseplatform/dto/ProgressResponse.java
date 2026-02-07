package com.learning.courseplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {
    private Long enrollmentId;
    private String courseId;
    private String courseTitle;
    private Integer totalSubtopics;
    private Integer completedSubtopics;
    private Double completionPercentage;
    private List<CompletedItem> completedItems;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompletedItem {
        private String subtopicId;
        private String subtopicTitle;
        private LocalDateTime completedAt;
    }
}
