package com.cherkizon.auth.repository;

import com.cherkizon.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isActive = true WHERE u.id = :userId")
    void activateUser(Long userId);
}