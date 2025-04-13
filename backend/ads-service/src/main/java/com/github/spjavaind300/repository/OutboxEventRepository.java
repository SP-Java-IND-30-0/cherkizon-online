package com.github.spjavaind300.repository;

import com.github.spjavaind300.model.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    Iterable<OutboxEvent> findAllByProcessedIsFalse();
}
