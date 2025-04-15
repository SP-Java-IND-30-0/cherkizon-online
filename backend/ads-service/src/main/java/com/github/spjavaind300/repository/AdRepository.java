package com.github.spjavaind300.repository;

import com.github.spjavaind300.model.dto.AdForNotificationService;
import com.github.spjavaind300.model.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {

    List<Ad> findAllByUserId(long userId);

    @Query("SELECT new com.github.spjavaind300.model.dto.AdForNotificationService(a.id, a.userId, a.title) FROM Ad a WHERE a.id = ?1")
    Optional<AdForNotificationService> getAdForNotifications(int id);
}
