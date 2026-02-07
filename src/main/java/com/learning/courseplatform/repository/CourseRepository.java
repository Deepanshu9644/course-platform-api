package com.learning.courseplatform.repository;

import com.learning.courseplatform.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    
    @Query("SELECT DISTINCT c FROM Course c " +
           "LEFT JOIN c.topics t " +
           "LEFT JOIN t.subtopics s " +
           "WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(s.content) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Course> searchCourses(@Param("query") String query);
}
