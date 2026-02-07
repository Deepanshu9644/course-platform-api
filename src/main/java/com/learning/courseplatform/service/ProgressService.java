package com.learning.courseplatform.service;

import com.learning.courseplatform.dto.SubtopicCompletionResponse;
import com.learning.courseplatform.entity.*;
import com.learning.courseplatform.exception.ForbiddenException;
import com.learning.courseplatform.exception.NotFoundException;
import com.learning.courseplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ProgressService {

    @Autowired
    private SubtopicProgressRepository subtopicProgressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubtopicRepository subtopicRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional
    public SubtopicCompletionResponse markSubtopicComplete(String subtopicId) {
        User user = getCurrentUser();
        
        Subtopic subtopic = subtopicRepository.findById(subtopicId)
                .orElseThrow(() -> new NotFoundException("Subtopic with id '" + subtopicId + "' does not exist"));

        // Get the course from the subtopic's topic
        String courseId = subtopic.getTopic().getCourse().getId();
        
        // Check if user is enrolled in the course
        if (!enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new ForbiddenException(
                "Forbidden", 
                "You must be enrolled in this course to mark subtopics as complete"
            );
        }

        // Find or create progress record
        SubtopicProgress progress = subtopicProgressRepository
                .findByUserIdAndSubtopicId(user.getId(), subtopicId)
                .orElse(new SubtopicProgress());

        if (progress.getId() == null) {
            progress.setUser(user);
            progress.setSubtopic(subtopic);
        }

        progress.setCompleted(true);
        if (progress.getCompletedAt() == null) {
            progress.setCompletedAt(LocalDateTime.now());
        }

        SubtopicProgress savedProgress = subtopicProgressRepository.save(progress);

        return new SubtopicCompletionResponse(
            subtopicId,
            savedProgress.getCompleted(),
            savedProgress.getCompletedAt()
        );
    }
}
