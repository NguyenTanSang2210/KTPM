package com.doanltmmt.Backend.controller;

import com.doanltmmt.Backend.entity.Student;
import com.doanltmmt.Backend.entity.Topic;
import com.doanltmmt.Backend.entity.TopicRegistration;
import com.doanltmmt.Backend.repository.StudentRepository;
import com.doanltmmt.Backend.repository.TopicRegistrationRepository;
import com.doanltmmt.Backend.repository.TopicRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/registration")
@CrossOrigin(origins = "http://localhost:5173")
public class TopicRegistrationController {

    private final TopicRepository topicRepo;
    private final StudentRepository studentRepo;
    private final TopicRegistrationRepository regRepo;

    public TopicRegistrationController(TopicRepository topicRepo, StudentRepository studentRepo,
                                       TopicRegistrationRepository regRepo) {
        this.topicRepo = topicRepo;
        this.studentRepo = studentRepo;
        this.regRepo = regRepo;
    }

    @PostMapping("/register")
    public TopicRegistration registerTopic(@RequestParam Long studentId,
                                           @RequestParam Long topicId) {

        Topic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        TopicRegistration reg = new TopicRegistration();
        reg.setTopic(topic);
        reg.setStudent(student);
        reg.setApproved(false);
        reg.setRegisteredAt(LocalDateTime.now());

        return regRepo.save(reg);
    }

    @GetMapping("/topic/{topicId}")
    public List<TopicRegistration> getRegistrations(@PathVariable Long topicId) {
        return regRepo.findByTopicId(topicId);
    }

    @PostMapping("/approve/{regId}")
    public TopicRegistration approve(@PathVariable Long regId) {
        TopicRegistration reg = regRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        reg.setApproved(true);
        return regRepo.save(reg);
    }

    @PostMapping("/reject/{regId}")
    public TopicRegistration reject(@PathVariable Long regId) {
        TopicRegistration reg = regRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        reg.setApproved(false);
        return regRepo.save(reg);
    }



}
