package com.github.spjavaind300.service.imp;

import com.github.spjavaind300.client.ProfileServiceClient;
import com.github.spjavaind300.exception.ProfileRequestFailedException;
import com.github.spjavaind300.model.dto.UserDto;
import com.github.spjavaind300.service.ProfileService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProfileServiceImp implements ProfileService {

    private final ProfileServiceClient client;

    public ProfileServiceImp(ProfileServiceClient client) {
        this.client = client;
    }


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
        try {
            return client.getProfiles(userId);
        } catch (FeignException e) {
            log.error("Feign error while getting user {}: status {}, headers {}, body {}",
                    userId,
                    e.status(),
                    e.responseHeaders(),
                    e.contentUTF8());
            throw e;
        }
    }

    @Recover
    public UserDto getUserFallback(long userId, Throwable t) {
        log.warn("getUserFallback for userId={}, error={}",
                userId,
                t == null ? "" : t.getMessage()
        );
        throw new ProfileRequestFailedException(userId, t);
    }

}
