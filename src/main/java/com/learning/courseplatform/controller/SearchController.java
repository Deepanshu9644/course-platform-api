package com.learning.courseplatform.controller;

import com.learning.courseplatform.dto.SearchResponse;
import com.learning.courseplatform.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
@Tag(name = "Search", description = "Search courses and content")
public class SearchController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    @Operation(summary = "Search courses", description = "Search for courses by title, description, topic, or content (public)")
    public ResponseEntity<SearchResponse> searchCourses(
            @Parameter(description = "Search query", example = "velocity")
            @RequestParam String q) {
        SearchResponse response = courseService.searchCourses(q);
        return ResponseEntity.ok(response);
    }
}
