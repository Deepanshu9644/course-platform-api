package com.learning.courseplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    private String query;
    private List<SearchResult> results;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResult {
        private String courseId;
        private String courseTitle;
        private List<Match> matches;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Match {
        private String type;
        private String topicTitle;
        private String subtopicId;
        private String subtopicTitle;
        private String snippet;
    }
}
