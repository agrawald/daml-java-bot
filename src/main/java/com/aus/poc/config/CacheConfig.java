package com.aus.poc.config;

import com.daml.ledger.javaapi.data.Event;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
    @Bean
    CacheManager cacheManager() {
        final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("contracts", CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class,
                        Event.class, ResourcePoolsBuilder.heap(1000)))
                .build();
        return cacheManager;
    }
}