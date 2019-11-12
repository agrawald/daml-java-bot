package com.aus.poc.config;

import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.rxjava.components.helpers.CreatedContract;

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
                .withCache("contracts", CacheConfigurationBuilder.newCacheConfigurationBuilder(Identifier.class,
                        CreatedContract.class, ResourcePoolsBuilder.heap(1000)))
                .build();
        return cacheManager;
    }
}