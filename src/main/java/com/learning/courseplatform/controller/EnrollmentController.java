package com.learning.courseplatform.controller;

import com.learning.courseplatform.dto.ProgressResponse;
import com.learning.courseplatform.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Enrollments", description = "View enrollment progress")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @GetMapping("/{enrollmentId}/progress")
    @Operation(summary = "View enrollment progress", description = "Get progress details for a specific enrollment (requires JWT)")
    public ResponseEntity<ProgressResponse> getEnrollmentProgress(@PathVariable Long enrollmentId) {
        ProgressResponse response = enrollmentService.getEnrollmentProgress(enrollmentId);
        return ResponseEntity.ok(response);
    }
}
