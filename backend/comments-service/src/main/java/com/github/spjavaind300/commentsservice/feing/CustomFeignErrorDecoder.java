package com.github.spjavaind300.commentsservice.feing;

import com.github.spjavaind300.commentsservice.exception.AdNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (methodKey.contains("AdsFeignClientInternal#checkAdExists") && response.status() == 404) {
            String adId = extractAdIdFromUrl(response.request().url());
            return new AdNotFoundException(Integer.parseInt(adId));
        }

        return defaultDecoder.decode(methodKey, response);
    }

    private String extractAdIdFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
}