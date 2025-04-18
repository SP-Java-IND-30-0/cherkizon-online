package com.github.spjavaind300.apigateway.filter;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.spjavaind300.apigateway.dto.TokenDto;
import com.github.spjavaind300.apigateway.utils.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BasicToJwtFilter implements GlobalFilter {

    private static final String AUTH_BASIC_PREFIX = "Basic ";
    private static final String AUTH_BEARER_PREFIX = "Bearer ";
    private static final String LOGIN_URL = "/api/auth/login";
    private static final String REFRESH_URL = "/api/auth/refresh";
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private final WebClient authWebClient;
    private final Cache<String, TokenDto> tokenCache;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(AUTH_BASIC_PREFIX)) {
            return chain.filter(exchange);
        }

        return processBasicAuth(exchange, chain, authHeader)
                .onErrorResume(e -> handleError(exchange, e));

    }

    private Mono<Void> processBasicAuth(ServerWebExchange exchange,
                                        GatewayFilterChain chain,
                                        String authHeader) {
        return Mono.just(authHeader)
                .map(this::extractCredentials)
                .flatMap(creds -> getOrRefreshToken(creds[0], creds[1]))
                .flatMap(jwt -> updateRequestHeaders(exchange, chain, jwt));
    }

    private String[] extractCredentials(String header) {
        try {
            String base64 = header.substring(AUTH_BASIC_PREFIX.length());
            String decoded = new String(Base64.getDecoder().decode(base64));
            return decoded.split(":", 2);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Basic Auth format");
        }
    }

    private Mono<String> getOrRefreshToken(String username, String password) {
        String cacheKey = Hashing.hashCredentials(username, password);
        TokenDto cached = tokenCache.getIfPresent(cacheKey);

        if (cached != null && !isTokenExpired(cached.expiresAt())) {
            return Mono.just(cached.accessToken());
        }
        return refreshOrAuthenticate(username, password, cacheKey);
    }

    private Mono<String> refreshOrAuthenticate(String username,
                                               String password,
                                               String cacheKey) {
        TokenDto cached = tokenCache.getIfPresent(cacheKey);

        if (cached != null && cached.refreshToken() != null) {
            return refreshToken(cached.refreshToken(), cacheKey)
                    .onErrorResume(e -> authenticate(username, password, cacheKey));
        }
        return authenticate(username, password, cacheKey);
    }

    private Mono<String> authenticate(String username,
                                      String password,
                                      String cacheKey) {
        return authWebClient.post()
                .uri(LOGIN_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(Map.of(USERNAME_KEY, username, PASSWORD_KEY, password))
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        this::handleAuthError
                )
                .bodyToMono(TokenDto.class)
                .flatMap(token -> cacheTokens(cacheKey, token));
    }

    private Mono<String> refreshToken(String refreshToken, String cacheKey) {
        return authWebClient.post()
                .uri(REFRESH_URL)
                .header(HttpHeaders.AUTHORIZATION, AUTH_BEARER_PREFIX + refreshToken)
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        this::handleAuthError
                )
                .bodyToMono(TokenDto.class)
                .flatMap(token -> cacheTokens(cacheKey, token));
    }

    private Mono<String> cacheTokens(String cacheKey, TokenDto token) {
        tokenCache.put(cacheKey, token);
        return Mono.just(token.accessToken());
    }

    private Mono<Void> updateRequestHeaders(ServerWebExchange exchange,
                                            GatewayFilterChain chain,
                                            String jwt) {
        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .headers(headers -> {
                    headers.remove(HttpHeaders.AUTHORIZATION);
                    headers.set(HttpHeaders.AUTHORIZATION, AUTH_BEARER_PREFIX + jwt);
                })
                .build();
        ServerWebExchange mutated = exchange.mutate()
                .request(mutatedRequest)
                .build();

        return chain.filter(mutated);
    }

    private boolean isTokenExpired(Instant expiry) {
        return Instant.now().plusSeconds(60).isAfter(expiry);
    }

    private Mono<? extends Throwable> handleAuthError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("Unknown auth error")
                .flatMap(errorBody -> Mono.error(new ResponseStatusException(response.statusCode(), errorBody)));
    }

    private Mono<Void> handleError(ServerWebExchange exchange, Throwable e) {
        log.error("Authentication failed: {}", e.getMessage(), e);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

}

