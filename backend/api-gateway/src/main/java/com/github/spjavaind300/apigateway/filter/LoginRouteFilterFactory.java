package com.github.spjavaind300.apigateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.spjavaind300.apigateway.dto.TokenDto;
import com.github.spjavaind300.apigateway.utils.Hashing;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class LoginRouteFilterFactory extends AbstractGatewayFilterFactory<LoginRouteFilterFactory.Config> {


    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private final WebClient authWebClient;
    private final ObjectMapper mapper;
    private final Cache<String, TokenDto> tokenCache;


    public LoginRouteFilterFactory(WebClient authWebClient, ObjectMapper mapper, Cache<String, TokenDto> tokenCache) {
        super(Config.class);
        this.authWebClient = authWebClient;
        this.mapper = mapper;
        this.tokenCache = tokenCache;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!isLoginRequest(exchange.getRequest())) {
                return chain.filter(exchange);
            }
            return exchange.getRequest()
                    .getBody()
                    .single()
                    .flatMap(buffer -> {
                        try {
                            String body = buffer.toString(StandardCharsets.UTF_8);
                            JsonNode json = mapper.readTree(body);

                            if (!json.has(USERNAME_KEY) || !json.has(PASSWORD_KEY)) {
                                return Mono.error(new IllegalArgumentException("Missing 'username' or 'password'"));
                            }
                            String username = json.get(USERNAME_KEY).asText();
                            String password = json.get(PASSWORD_KEY).asText();

                            return authenticateAndCache(username, password, config.getLoginUrl())
//                                .then(chain.filter(exchange))
                                    .then(Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.OK)))
                                    .doOnSuccess(v -> log.info("Cached tokens for user: {}", username));
                        } catch (Exception e) {
                            return Mono.error(e);
                        }
                    })
                    .onErrorResume(e -> handleError(exchange, e)).then();
        };
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("loginUrl");
    }

    @Data
    public static class Config {
        String loginUrl = "/api/auth/login";
    }


    private boolean isLoginRequest(@NotNull ServerHttpRequest request) {
        return HttpMethod.POST.equals(request.getMethod())
                && "/login".equals(request.getPath().toString());
    }

    private Mono<Void> authenticateAndCache(String username, String password, String loginUrl) {
        return authWebClient.post()
                .uri(loginUrl)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(Map.of(USERNAME_KEY, username, PASSWORD_KEY, password))
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        this::handleAuthError
                )
                .bodyToMono(TokenDto.class)
                .flatMap(token -> {
                    if (token.accessToken() == null
                            || token.refreshToken() == null
                            || token.accessToken().isEmpty()
                            || token.refreshToken().isEmpty()) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                    }

                    String cacheKey = Hashing.hashCredentials(username, password);
                    tokenCache.put(cacheKey, token);
                    return Mono.empty();
                })
                .doOnError(e -> log.error("Error caching tokens for user: {}", username, e))
                .then();

    }


    private Mono<? extends Throwable> handleAuthError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .defaultIfEmpty("Unknown auth error")
                .flatMap(errorBody -> Mono.error(new ResponseStatusException(response.statusCode(), errorBody)));
    }

    private Mono<Void> handleError(ServerWebExchange exchange, Throwable e) {
        log.error("Login error", e);

        if (e instanceof IllegalArgumentException) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String errorBody = mapper.createObjectNode().put("error", e.getMessage()).toString();
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap(errorBody.getBytes())));
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }
}
