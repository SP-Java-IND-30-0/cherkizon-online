package com.github.spjavaind300.commentsservice.security;

import com.github.spjavaind300.commentsservice.exception.InvalidJwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import com.github.spjavaind300.commentsservice.dto.Role;
import com.github.spjavaind300.commentsservice.dto.UserContext;
import com.github.spjavaind300.commentsservice.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtUtilsImpl implements JwtUtils {

    private final String JwtSecret;

    @Override
    public UserContext getUserContext() {
        UserContext context = UserContextHolder.getContext();
        if (context == null) {
            throw new UnauthorizedException();
        }
        return context;
    }

    @Override
    public long getUserId(String token) {
        try {
            return Long.parseLong(extractClaim(token, Claims::getSubject));
        } catch (Exception e) {
            throw new InvalidJwtException(e);
        }
    }

    @Override
    public Role getRole(String token) {
        try {
            return Role.valueOf(extractClaim(token, claims -> claims.get("role", String.class)));
        } catch (Exception e) {
            throw new InvalidJwtException(e);
        }
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
}