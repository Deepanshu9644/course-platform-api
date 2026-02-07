package com.learning.courseplatform.service;

import com.learning.courseplatform.dto.EnrollmentResponse;
import com.learning.courseplatform.dto.ProgressResponse;
import com.learning.courseplatform.entity.*;
import com.learning.courseplatform.exception.ConflictException;
import com.learning.courseplatform.exception.ForbiddenException;
import com.learning.courseplatform.exception.NotFoundException;
import com.learning.courseplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubtopicProgressRepository subtopicProgressRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Transactional
    public EnrollmentResponse enrollInCourse(String courseId) {
        User user = getCurrentUser();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course with id '" + courseId + "' does not exist"));

        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new ConflictException("Already enrolled", "You are already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        return new EnrollmentResponse(
            savedEnrollment.getId(),
            course.getId(),
            course.getTitle(),
            savedEnrollment.getEnrolledAt()
        );
    }

    @Transactional(readOnly = true)
    public ProgressResponse getEnrollmentProgress(Long enrollmentId) {
        User user = getCurrentUser();
        
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found"));

        if (!enrollment.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Access denied", "You can only view your own progress");
        }

        Course course = enrollment.getCourse();
        int totalSubtopics = course.getSubtopicCount();

        List<SubtopicProgress> progressList = subtopicProgressRepository
                .findByUserIdAndCourseId(user.getId(), course.getId());

        List<SubtopicProgress> completedList = progressList.stream()
                .filter(SubtopicProgress::getCompleted)
                .collect(Collectors.toList());

        int completedCount = completedList.size();
        double percentage = totalSubtopics > 0 
            ? Math.round((completedCount * 100.0 / totalSubtopics) * 100.0) / 100.0 
            : 0.0;

        List<ProgressResponse.CompletedItem> completedItems = completedList.stream()
                .map(sp -> new ProgressResponse.CompletedItem(
                    sp.getSubtopic().getId(),
                    sp.getSubtopic().getTitle(),
                    sp.getCompletedAt()
                ))
                .collect(Collectors.toList());

        return new ProgressResponse(
            enrollment.getId(),
            course.getId(),
            course.getTitle(),
            totalSubtopics,
            completedCount,
            percentage,
            completedItems
        );
    }
}
