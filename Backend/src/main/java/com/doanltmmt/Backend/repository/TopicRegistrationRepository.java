package com.doanltmmt.Backend.repository;

import com.doanltmmt.Backend.entity.TopicRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TopicRegistrationRepository extends JpaRepository<TopicRegistration, Long> {

    List<TopicRegistration> findByTopic_Id(Long topicId);

    Optional<TopicRegistration> findTopByStudent_IdAndTopic_IdOrderByRegisteredAtDesc(Long studentId, Long topicId);

    List<TopicRegistration> findByStudent_IdOrderByRegisteredAtDesc(Long studentId);

    boolean existsByTopic_IdAndApprovedTrue(Long topicId);

    long countByTopic_Id(Long topicId);

    long countByTopic_IdAndApprovedIsNull(Long topicId);
    long countByTopic_IdAndApprovedTrue(Long topicId);
    long countByTopic_IdAndApprovedFalse(Long topicId);

}
