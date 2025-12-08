package com.doanltmmt.Backend.controller;

import com.doanltmmt.Backend.entity.Lecturer;
import com.doanltmmt.Backend.entity.Topic;
import com.doanltmmt.Backend.repository.LecturerRepository;
import com.doanltmmt.Backend.repository.TopicRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@CrossOrigin(origins = "http://localhost:5173")
public class TopicController {

    private final TopicRepository topicRepo;
    private final LecturerRepository lecturerRepo;

    public TopicController(TopicRepository topicRepo, LecturerRepository lecturerRepo) {
        this.topicRepo = topicRepo;
        this.lecturerRepo = lecturerRepo;
    }

    // Lấy danh sách đề tài
    @GetMapping
    public List<Topic> getAll() {
        return topicRepo.findAll();
    }

    // Giảng viên tạo đề tài
    @PostMapping("/create")
    public Topic createTopic(@RequestParam Long lecturerId, @RequestBody Topic topic) {
        Lecturer lecturer = lecturerRepo.findById(lecturerId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));

        topic.setLecturer(lecturer);
        topic.setStatus("OPEN");

        return topicRepo.save(topic);
    }
}
