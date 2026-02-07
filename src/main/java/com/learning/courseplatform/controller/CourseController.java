package com.learning.courseplatform.controller;

import com.learning.courseplatform.dto.CourseDetailResponse;
import com.learning.courseplatform.dto.CourseListResponse;
import com.learning.courseplatform.dto.EnrollmentResponse;
import com.learning.courseplatform.service.CourseService;
import com.learning.courseplatform.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "Browse and enroll in courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping
    @Operation(summary = "List all courses", description = "Get a list of all available courses (public)")
    public ResponseEntity<CourseListResponse> getAllCourses() {
        CourseListResponse response = courseService.getAllCourses();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Get course by ID", description = "Get detailed information about a specific course (public)")
    public ResponseEntity<CourseDetailResponse> getCourseById(@PathVariable String courseId) {
        CourseDetailResponse response = courseService.getCourseById(courseId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{courseId}/enroll")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Enroll in course", description = "Enroll the authenticated user in a course (requires JWT)")
    public ResponseEntity<EnrollmentResponse> enrollInCourse(@PathVariable String courseId) {
        EnrollmentResponse response = enrollmentService.enrollInCourse(courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
