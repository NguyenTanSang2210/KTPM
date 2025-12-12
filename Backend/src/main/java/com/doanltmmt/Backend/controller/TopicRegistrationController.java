package com.doanltmmt.Backend.controller;

import com.doanltmmt.Backend.entity.Student;
import com.doanltmmt.Backend.entity.Topic;
import com.doanltmmt.Backend.entity.TopicRegistration;
import com.doanltmmt.Backend.repository.StudentRepository;
import com.doanltmmt.Backend.repository.LecturerRepository;
import com.doanltmmt.Backend.repository.UserRepository;
import com.doanltmmt.Backend.entity.User;
import com.doanltmmt.Backend.repository.TopicRegistrationRepository;
import com.doanltmmt.Backend.repository.TopicRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/registration")
@CrossOrigin(origins = "http://localhost:5173")
public class TopicRegistrationController {

    private final TopicRepository topicRepo;
    private final StudentRepository studentRepo;
    private final TopicRegistrationRepository regRepo;
    private final LecturerRepository lecturerRepo;
    private final UserRepository userRepo;

    public TopicRegistrationController(TopicRepository topicRepo, StudentRepository studentRepo,
                                       TopicRegistrationRepository regRepo,
                                       LecturerRepository lecturerRepo,
                                       UserRepository userRepo) {
        this.topicRepo = topicRepo;
        this.studentRepo = studentRepo;
        this.regRepo = regRepo;
        this.lecturerRepo = lecturerRepo;
        this.userRepo = userRepo;
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('STUDENT')")
    public TopicRegistration registerTopic(@RequestParam Long studentId,
                                           @RequestParam Long topicId) {

        Topic topic = topicRepo.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        // Chỉ cho đăng ký khi đề tài đang mở
        if (!"OPEN".equalsIgnoreCase(topic.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic is not open for registration");
        }

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        TopicRegistration reg = new TopicRegistration();
        reg.setTopic(topic);
        reg.setStudent(student);
        reg.setApproved(null); // chờ duyệt
        reg.setRegisteredAt(LocalDateTime.now());

        return regRepo.save(reg);
    }

    @GetMapping("/topic/{topicId}")
    @PreAuthorize("hasRole('LECTURER')")
    public List<TopicRegistration> getRegistrations(@PathVariable Long topicId) {
        return regRepo.findByTopic_Id(topicId);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('STUDENT')")
    public List<TopicRegistration> myRegistrations(@RequestParam Long studentId) {
        return regRepo.findByStudent_IdOrderByRegisteredAtDesc(studentId);
    }

    @PostMapping("/approve/{regId}")
    @PreAuthorize("hasRole('LECTURER')")
    public TopicRegistration approve(@PathVariable Long regId) {
        TopicRegistration reg = regRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        // Không cho duyệt nếu đề tài đã có sinh viên được duyệt
        Long topicId = reg.getTopic().getId();
        if (regRepo.existsByTopic_IdAndApprovedTrue(topicId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Topic already has an approved registration");
        }

        reg.setApproved(true);
        // Lưu nhật ký: người duyệt và thời gian
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            String username = auth.getName();
            User u = userRepo.findByUsername(username).orElse(null);
            if (u != null) {
                com.doanltmmt.Backend.entity.Lecturer reviewer = lecturerRepo.findById(u.getId()).orElse(null);
                reg.setReviewer(reviewer);
                reg.setReviewedAt(java.time.LocalDateTime.now());
            }
        }
        // Cập nhật trạng thái đề tài sang REGISTERED khi duyệt
        Topic topic = reg.getTopic();
        topic.setStatus("REGISTERED");
        topicRepo.save(topic);
        return regRepo.save(reg);
    }

    @PostMapping("/reject/{regId}")
    @PreAuthorize("hasRole('LECTURER')")
    public TopicRegistration reject(@PathVariable Long regId,
                                    @RequestParam(required = false) String reason) {
        TopicRegistration reg = regRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        reg.setApproved(false);
        reg.setRejectReason(reason);
        // Lưu nhật ký: người từ chối và thời gian
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            String username = auth.getName();
            User u = userRepo.findByUsername(username).orElse(null);
            if (u != null) {
                com.doanltmmt.Backend.entity.Lecturer reviewer = lecturerRepo.findById(u.getId()).orElse(null);
                reg.setReviewer(reviewer);
                reg.setReviewedAt(java.time.LocalDateTime.now());
            }
        }
        return regRepo.save(reg);
    }

    @PostMapping("/cancel/{regId}")
    @PreAuthorize("hasRole('STUDENT')")
    public void cancel(@PathVariable Long regId) {
        TopicRegistration reg = regRepo.findById(regId)
                .orElseThrow(() -> new RuntimeException("Registration not found"));

        // Chỉ cho hủy khi đang chờ duyệt
        if (reg.getApproved() != null) {
            throw new RuntimeException("Cannot cancel a processed registration");
        }
        regRepo.deleteById(regId);
    }
}
