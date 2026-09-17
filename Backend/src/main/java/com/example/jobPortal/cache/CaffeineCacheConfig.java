package com.example.jobPortal.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineCacheConfig {

    public CacheManager caffeineCacheManager() {
        CaffeineCache companyCache = new CaffeineCache("companies", Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(500).build());
        CaffeineCache jobsCache = new CaffeineCache("jobs",Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES).maximumSize(5000).build());
        CaffeineCache rolesCache = new CaffeineCache("roles",Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS).maximumSize(100).build());

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(Arrays.asList(companyCache, jobsCache, rolesCache));
        return manager;
    }
}
