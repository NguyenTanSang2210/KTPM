package com.doanltmmt.Backend.repository;

import com.doanltmmt.Backend.entity.TopicRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRegistrationRepository extends JpaRepository<TopicRegistration, Long> {

    List<TopicRegistration> findByTopicId(Long topicId);

}
