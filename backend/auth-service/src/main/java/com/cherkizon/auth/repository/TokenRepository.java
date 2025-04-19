package com.cherkizon.auth.repository;

import com.cherkizon.auth.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllByUserId(Long userId);

}
