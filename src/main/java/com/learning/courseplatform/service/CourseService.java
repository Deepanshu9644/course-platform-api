package com.learning.courseplatform.service;

import com.learning.courseplatform.dto.*;
import com.learning.courseplatform.entity.Course;
import com.learning.courseplatform.entity.Subtopic;
import com.learning.courseplatform.entity.Topic;
import com.learning.courseplatform.exception.NotFoundException;
import com.learning.courseplatform.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public CourseListResponse getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        
        List<CourseListResponse.CourseSummary> summaries = courses.stream()
            .map(course -> new CourseListResponse.CourseSummary(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getTopicCount(),
                course.getSubtopicCount()
            ))
            .collect(Collectors.toList());

        return new CourseListResponse(summaries);
    }

    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseById(String courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with id '" + courseId + "' does not exist"));

        List<CourseDetailResponse.TopicDetail> topics = course.getTopics().stream()
            .map(topic -> {
                List<CourseDetailResponse.SubtopicDetail> subtopics = topic.getSubtopics().stream()
                    .map(subtopic -> new CourseDetailResponse.SubtopicDetail(
                        subtopic.getId(),
                        subtopic.getTitle(),
                        subtopic.getContent()
                    ))
                    .collect(Collectors.toList());

                return new CourseDetailResponse.TopicDetail(
                    topic.getId(),
                    topic.getTitle(),
                    subtopics
                );
            })
            .collect(Collectors.toList());

        return new CourseDetailResponse(
            course.getId(),
            course.getTitle(),
            course.getDescription(),
            topics
        );
    }

    @Transactional(readOnly = true)
    public SearchResponse searchCourses(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new SearchResponse(query, new ArrayList<>());
        }

        List<Course> courses = courseRepository.searchCourses(query.trim());
        
        List<SearchResponse.SearchResult> results = courses.stream()
            .map(course -> buildSearchResult(course, query))
            .filter(result -> !result.getMatches().isEmpty())
            .collect(Collectors.toList());

        return new SearchResponse(query, results);
    }

    private SearchResponse.SearchResult buildSearchResult(Course course, String query) {
        List<SearchResponse.Match> matches = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Topic topic : course.getTopics()) {
            for (Subtopic subtopic : topic.getSubtopics()) {
                // Check subtopic title
                if (subtopic.getTitle().toLowerCase().contains(lowerQuery)) {
                    matches.add(new SearchResponse.Match(
                        "subtopic",
                        topic.getTitle(),
                        subtopic.getId(),
                        subtopic.getTitle(),
                        truncateText(subtopic.getContent(), 150)
                    ));
                }
                // Check subtopic content
                else if (subtopic.getContent() != null && 
                         subtopic.getContent().toLowerCase().contains(lowerQuery)) {
                    String snippet = extractSnippet(subtopic.getContent(), query);
                    matches.add(new SearchResponse.Match(
                        "content",
                        topic.getTitle(),
                        subtopic.getId(),
                        subtopic.getTitle(),
                        snippet
                    ));
                }
            }
        }

        return new SearchResponse.SearchResult(
            course.getId(),
            course.getTitle(),
            matches
        );
    }

    private String extractSnippet(String content, String query) {
        String lowerContent = content.toLowerCase();
        String lowerQuery = query.toLowerCase();
        
        int index = lowerContent.indexOf(lowerQuery);
        if (index == -1) {
            return truncateText(content, 150);
        }

        int start = Math.max(0, index - 50);
        int end = Math.min(content.length(), index + query.length() + 100);
        
        String snippet = content.substring(start, end);
        if (start > 0) snippet = "..." + snippet;
        if (end < content.length()) snippet = snippet + "...";
        
        return snippet.trim();
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}
