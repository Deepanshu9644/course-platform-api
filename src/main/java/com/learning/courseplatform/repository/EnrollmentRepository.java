package com.learning.courseplatform.repository;

import com.learning.courseplatform.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, String courseId);
    List<Enrollment> findByUserId(Long userId);
    boolean existsByUserIdAndCourseId(Long userId, String courseId);
}
