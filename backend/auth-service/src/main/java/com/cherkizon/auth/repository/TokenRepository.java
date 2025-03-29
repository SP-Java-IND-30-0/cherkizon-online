package com.cherkizon.auth.repository;

import com.cherkizon.auth.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("""
        select t from Token t
        where t.user.id = :userId
        and t.revoked = false
        and t.expiryDate > current_timestamp
    """)
    List<Token> findAllValidTokensByUser(Long userId);
    Optional<Token> findByToken(String token);

    boolean existsByToken(String token);
    @Modifying
    @Query("update Token t set t.revoked = true where t.user.id = :userId")
    void revokeAllUserTokens(Long userId);
}