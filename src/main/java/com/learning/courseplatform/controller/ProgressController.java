package com.learning.courseplatform.controller;

import com.learning.courseplatform.dto.SubtopicCompletionResponse;
import com.learning.courseplatform.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subtopics")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Progress", description = "Track learning progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @PostMapping("/{subtopicId}/complete")
    @Operation(summary = "Mark subtopic as completed", description = "Mark a subtopic as completed (requires JWT and enrollment)")
    public ResponseEntity<SubtopicCompletionResponse> markSubtopicComplete(@PathVariable String subtopicId) {
        SubtopicCompletionResponse response = progressService.markSubtopicComplete(subtopicId);
        return ResponseEntity.ok(response);
    }
}
