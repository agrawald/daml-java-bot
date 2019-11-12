package com.aus.poc.repo;

import java.util.function.Function;
import java.util.stream.StreamSupport;

import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.rxjava.components.helpers.CreatedContract;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractCache implements Function<CreatedContract, CreatedContract>, InitializingBean, DisposableBean {
    private final CacheManager cacheManager;
    public Cache<Identifier, CreatedContract> getCache() {
        return this.cacheManager.getCache("contracts", Identifier.class, CreatedContract.class);
    }

    @Override
    public CreatedContract apply(CreatedContract t) {
        log.info("PackageId: {}", t);
        this.getCache().putIfAbsent(t.getTemplateId(), t);
        return t;
    }

    @Scheduled(fixedDelay = 5000)
    public void size() {
        log.info("Size of cache: {}", StreamSupport.stream(this.getCache().spliterator(), true)
        .count());
    }

    @Override
    public void destroy() throws Exception {
        log.info("Removing the active contract cache");
        cacheManager.removeCache("contracts");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Initializing the active contract cache");
        cacheManager.init();
    }

}