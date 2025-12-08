package com.doanltmmt.Backend.repository;

import com.doanltmmt.Backend.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Long> {
}
