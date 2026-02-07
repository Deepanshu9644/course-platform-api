package com.learning.courseplatform.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.courseplatform.entity.Course;
import com.learning.courseplatform.entity.Subtopic;
import com.learning.courseplatform.entity.Topic;
import com.learning.courseplatform.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if database already has data
        if (courseRepository.count() > 0) {
            logger.info("Database already contains data. Skipping seed data load.");
            return;
        }

        logger.info("Loading seed data from JSON file...");
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream inputStream = new ClassPathResource("seed-data.json").getInputStream();
            JsonNode root = mapper.readTree(inputStream);
            JsonNode coursesNode = root.get("courses");

            List<Course> courses = new ArrayList<>();

            for (JsonNode courseNode : coursesNode) {
                Course course = new Course();
                course.setId(courseNode.get("id").asText());
                course.setTitle(courseNode.get("title").asText());
                course.setDescription(courseNode.get("description").asText());
                
                List<Topic> topics = new ArrayList<>();
                JsonNode topicsNode = courseNode.get("topics");

                for (JsonNode topicNode : topicsNode) {
                    Topic topic = new Topic();
                    topic.setId(topicNode.get("id").asText());
                    topic.setTitle(topicNode.get("title").asText());
                    topic.setCourse(course);
                    
                    List<Subtopic> subtopics = new ArrayList<>();
                    JsonNode subtopicsNode = topicNode.get("subtopics");

                    for (JsonNode subtopicNode : subtopicsNode) {
                        Subtopic subtopic = new Subtopic();
                        subtopic.setId(subtopicNode.get("id").asText());
                        subtopic.setTitle(subtopicNode.get("title").asText());
                        subtopic.setContent(subtopicNode.get("content").asText());
                        subtopic.setTopic(topic);
                        
                        subtopics.add(subtopic);
                    }

                    topic.setSubtopics(subtopics);
                    topics.add(topic);
                }

                course.setTopics(topics);
                courses.add(course);
            }

            courseRepository.saveAll(courses);
            logger.info("Seed data loaded successfully! {} courses loaded.", courses.size());
            
        } catch (Exception e) {
            logger.error("Error loading seed data: ", e);
            throw e;
        }
    }
}
