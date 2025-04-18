package com.github.spjavaind300.profileservice.repository;

import com.github.spjavaind300.profileservice.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
