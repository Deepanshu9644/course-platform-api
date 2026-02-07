package com.learning.courseplatform.repository;

import com.learning.courseplatform.entity.Subtopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubtopicRepository extends JpaRepository<Subtopic, String> {
}
