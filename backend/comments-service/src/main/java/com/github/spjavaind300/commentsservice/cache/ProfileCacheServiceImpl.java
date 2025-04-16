package com.github.spjavaind300.commentsservice.cache;

import com.github.spjavaind300.commentsservice.feing.ProfileFeignClientInternal;
import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import com.github.spjavaind300.commentsservice.exception.ExternalServiceException;
import com.github.spjavaind300.commentsservice.exception.NotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileCacheServiceImpl implements ProfileCacheService {

    private final ProfileFeignClientInternal profileFeignClient;
    private final AuthorProfileCache authorProfileCache;

    @Override
    public ProfileDto getProfile(long authorId) {
        ProfileDto cachedProfile = authorProfileCache.get(authorId);
        if (cachedProfile != null) {
            return cachedProfile;
        }

        try {
            ProfileDto profile = profileFeignClient.getProfileByIdInternal(authorId);

            authorProfileCache.put(authorId, profile);
            return profile;

        } catch (FeignException.NotFound e) {
            throw new NotFoundException("Профиль", authorId);
        } catch (FeignException e) {
            throw new ExternalServiceException(e.getMessage(), e);
        }
    }
}