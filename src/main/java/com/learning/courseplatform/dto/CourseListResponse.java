package com.learning.courseplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseListResponse {
    private List<CourseSummary> courses;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseSummary {
        private String id;
        private String title;
        private String description;
        private int topicCount;
        private int subtopicCount;
    }
}
