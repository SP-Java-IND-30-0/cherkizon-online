package com.github.spjavaind300.commentsservice.cache;

import com.github.spjavaind300.commentsservice.feing.ProfileFeignClientInternal;
import com.github.spjavaind300.commentsservice.dto.ProfileDto;
import com.github.spjavaind300.commentsservice.exception.ExternalServiceException;
import com.github.spjavaind300.commentsservice.exception.NotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ProfileCacheService} that retrieves user profiles using a caching mechanism.
 * If the profile is not found in the local cache, it fetches it from the profile service via a Feign client.
 * Handles errors related to profile retrieval and caching.
 */
@Service
@RequiredArgsConstructor
public class ProfileCacheServiceImpl implements ProfileCacheService {

    private final ProfileFeignClientInternal profileFeignClient;
    private final AuthorProfileCache authorProfileCache;

    /**
     * Retrieves a user profile by author ID. First attempts to retrieve the profile from the cache.
     * If not found, calls the external profile service and caches the result.
     *
     * @param authorId the ID of the author whose profile is being requested
     * @return a {@link ProfileDto} containing the author's profile information
     * @throws NotFoundException if the profile is not found in the external service
     * @throws ExternalServiceException if an error occurs while calling the external service
     */
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
            throw new NotFoundException(ProfileDto.class, authorId);
        } catch (FeignException e) {
            throw new ExternalServiceException(e.getMessage(), e);
        }
    }
}