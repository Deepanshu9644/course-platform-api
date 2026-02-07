package com.learning.courseplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailResponse {
    private String id;
    private String title;
    private String description;
    private List<TopicDetail> topics;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicDetail {
        private String id;
        private String title;
        private List<SubtopicDetail> subtopics;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubtopicDetail {
        private String id;
        private String title;
        private String content;
    }
}
