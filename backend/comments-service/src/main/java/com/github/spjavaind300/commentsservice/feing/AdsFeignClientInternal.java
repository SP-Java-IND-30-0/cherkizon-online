package com.github.spjavaind300.commentsservice.feing;

import com.github.spjavaind300.commentsservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ads-service", configuration = FeignConfig.class)
public interface AdsFeignClientInternal {

    @GetMapping("/internal/ads/{adId}")
    void checkAdExists(@PathVariable("adId") int adId);
}