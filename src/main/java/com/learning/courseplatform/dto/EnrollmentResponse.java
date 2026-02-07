package com.learning.courseplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long enrollmentId;
    private String courseId;
    private String courseTitle;
    private LocalDateTime enrolledAt;
}
