package com.aus.poc.service;

import com.daml.ledger.rxjava.components.helpers.CreatedContract;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActiveContractSvc {
    private final CacheManager cacheManager;

    private Cache<String, CreatedContract> getCache() {
        return this.cacheManager.getCache("contracts", String.class, CreatedContract.class);
    }

    public CreatedContract get(String entityName) {
        return this.getCache().containsKey(entityName) ?
        this.getCache().get(entityName) : null;
    }
}