package com.doanltmmt.Backend.controller;

import com.doanltmmt.Backend.entity.Lecturer;
import com.doanltmmt.Backend.entity.Topic;
import com.doanltmmt.Backend.entity.User;
import com.doanltmmt.Backend.entity.TopicRegistration;
import com.doanltmmt.Backend.repository.LecturerRepository;
import com.doanltmmt.Backend.repository.TopicRepository;
import com.doanltmmt.Backend.repository.TopicRegistrationRepository;
import com.doanltmmt.Backend.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/topics")
@CrossOrigin(origins = "http://localhost:5173")
public class TopicController {

    private final TopicRepository topicRepo;
    private final LecturerRepository lecturerRepo;
    private final TopicRegistrationRepository regRepo;
    private final UserRepository userRepo;

    public TopicController(TopicRepository topicRepo,
                           LecturerRepository lecturerRepo,
                           TopicRegistrationRepository regRepo,
                           UserRepository userRepo) {
        this.topicRepo = topicRepo;
        this.lecturerRepo = lecturerRepo;
        this.regRepo = regRepo;
        this.userRepo = userRepo;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    public Topic createTopic(@RequestParam Long lecturerId, @RequestBody Topic topic) {
        // Ensure a Lecturer entity exists for the given user id. If missing, create it on-the-fly.
        Lecturer lecturer = lecturerRepo.findById(lecturerId).orElseGet(() -> {
            User user = userRepo.findById(lecturerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found for lecturerId"));
            Lecturer newLecturer = new Lecturer();
            newLecturer.setUser(user);
            return lecturerRepo.save(newLecturer);
        });

        topic.setLecturer(lecturer);
        topic.setStatus("OPEN");

        return topicRepo.save(topic);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    public Topic updateTopic(@PathVariable Long id,
                             @RequestParam(required = false) Long lecturerId,
                             @RequestBody Topic payload) {
        Topic topic = topicRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
        if (!canModifyTopic(topic, lecturerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied to update topic");
        }
        if (payload.getTitle() != null) topic.setTitle(payload.getTitle());
        if (payload.getDescription() != null) topic.setDescription(payload.getDescription());
        if (payload.getStatus() != null) topic.setStatus(payload.getStatus());
        return topicRepo.save(topic);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    public void deleteTopic(@PathVariable Long id,
                            @RequestParam(required = false) Long lecturerId) {
        Topic topic = topicRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
        if (!canModifyTopic(topic, lecturerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied to delete topic");
        }
        topicRepo.deleteById(id);
    }

    @PostMapping("/{id}/open")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    public Topic openTopic(@PathVariable Long id,
                           @RequestParam(required = false) Long lecturerId) {
        Topic topic = topicRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
        if (!canModifyTopic(topic, lecturerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied to open topic");
        }
        topic.setStatus("OPEN");
        return topicRepo.save(topic);
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('LECTURER','ADMIN')")
    public Topic closeTopic(@PathVariable Long id,
                            @RequestParam(required = false) Long lecturerId) {
        Topic topic = topicRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found"));
        if (!canModifyTopic(topic, lecturerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Permission denied to close topic");
        }
        topic.setStatus("CLOSED");
        return topicRepo.save(topic);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('STUDENT','LECTURER','ADMIN')")
    public Map<String, Object> getAll(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false, name = "query") String queryText,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long lecturerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        org.springframework.data.jpa.domain.Specification<Topic> spec = org.springframework.data.jpa.domain.Specification.where(null);

        if (status != null && !status.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        }
        if (lecturerId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("lecturer").get("id"), lecturerId));
        }
        if (queryText != null && !queryText.isBlank()) {
            String qLower = "%" + queryText.toLowerCase() + "%";
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("title")), qLower));
        }

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));

        org.springframework.data.domain.Page<Topic> pageRes = topicRepo.findAll(spec, pageable);

        List<Map<String, Object>> items = new ArrayList<>();
        for (Topic topic : pageRes.getContent()) {
            Map<String, Object> topicMap = new HashMap<>();
            topicMap.put("id", topic.getId());
            topicMap.put("title", topic.getTitle());
            topicMap.put("description", topic.getDescription());
            topicMap.put("status", topic.getStatus());
            topicMap.put("lecturer", topic.getLecturer());
            long regCount = regRepo.countByTopic_Id(topic.getId());
            long pendingCount = regRepo.countByTopic_IdAndApprovedIsNull(topic.getId());
            long approvedCount = regRepo.countByTopic_IdAndApprovedTrue(topic.getId());
            long rejectedCount = regRepo.countByTopic_IdAndApprovedFalse(topic.getId());
            topicMap.put("registrationCount", regCount);
            topicMap.put("pendingCount", pendingCount);
            topicMap.put("approvedCount", approvedCount);
            topicMap.put("rejectedCount", rejectedCount);

            String regStatus = "CHUA_DANG_KY";
            if (studentId != null) {
                Optional<TopicRegistration> regOpt = regRepo.findTopByStudent_IdAndTopic_IdOrderByRegisteredAtDesc(studentId, topic.getId());
                if (regOpt.isPresent()) {
                    TopicRegistration reg = regOpt.get();
                    Boolean approved = reg.getApproved();
                    if (approved == null) {
                        regStatus = "CHO_DUYET";
                    } else if (approved) {
                        regStatus = "DA_DUYET";
                    } else {
                        regStatus = "TU_CHOI";
                    }
                }
            }

            topicMap.put("registrationStatus", regStatus);
            items.add(topicMap);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("page", pageRes.getNumber());
        response.put("size", pageRes.getSize());
        response.put("totalElements", pageRes.getTotalElements());
        response.put("totalPages", pageRes.getTotalPages());
        return response;
    }

    private boolean canModifyTopic(Topic topic, Long lecturerId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> {
            String r = a.getAuthority();
            return "ROLE_ADMIN".equals(r) || "ADMIN".equals(r);
        });
        if (isAdmin) return true;
        if (lecturerId == null) return false;
        boolean isLecturer = auth.getAuthorities().stream().anyMatch(a -> {
            String r = a.getAuthority();
            return "ROLE_LECTURER".equals(r) || "LECTURER".equals(r);
        });
        if (!isLecturer) return false;
        return topic.getLecturer() != null && Objects.equals(topic.getLecturer().getId(), lecturerId);
    }
}
