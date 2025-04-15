package com.github.spjavaind300.repository;

import com.github.spjavaind300.model.entity.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {

    List<Ad> findAllByUserId(long userId);
}
