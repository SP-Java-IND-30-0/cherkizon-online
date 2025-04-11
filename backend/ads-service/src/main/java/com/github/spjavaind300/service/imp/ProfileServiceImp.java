package com.github.spjavaind300.service.imp;

import com.github.spjavaind300.client.ProfileServiceClient;
import com.github.spjavaind300.exception.ProfileRequestFailedException;
import com.github.spjavaind300.model.dto.UserDto;
import com.github.spjavaind300.service.ProfileService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileServiceImp implements ProfileService {

    private final ProfileServiceClient client;


    @Retryable(
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {FeignException.class},
            noRetryFor = {
                    FeignException.FeignClientException.BadRequest.class,
                    FeignException.FeignClientException.NotFound.class
            }
    )
    @Override
    public UserDto getUser(long userId) {
        return client.getProfiles(userId);
    }

    @Recover
    public void getUserFallback(long userId, Throwable t) {
        log.warn("getUserFallback for userId={}, error={}", userId, t.getMessage());
        throw new ProfileRequestFailedException(userId, t);
    }

}
