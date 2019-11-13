package com.aus.poc.repo;

import java.util.stream.StreamSupport;

import com.daml.ledger.javaapi.data.ArchivedEvent;
import com.daml.ledger.javaapi.data.CreatedEvent;
import com.daml.ledger.javaapi.data.Event;

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
public class ContractRepo implements io.reactivex.functions.Consumer<Event>, InitializingBean, DisposableBean {
    private final CacheManager cacheManager;

    public Cache<String, Event> getCache() {
        return this.cacheManager.getCache("contracts", String.class, Event.class);
    }

    @Scheduled(fixedDelay = 5000)
    public void size() {
        log.info("Size of cache: {}", StreamSupport.stream(this.getCache().spliterator(), true).count());
    }

    @Override
    public void destroy() throws Exception {
        log.info("Removing the active contract cache");
        cacheManager.removeCache("contracts");
    }

    public Event get(String constractId) {
        return this.getCache().containsKey(constractId) ? this.getCache().get(constractId) : null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Initializing the active contract cache");
        cacheManager.init();
    }

    @Override
    public void accept(Event event) {
        log.info("{} -> Contract Id: {} Event: {}", event.getClass(), event.getContractId(), event);
        if (event instanceof CreatedEvent) {
            this.getCache().putIfAbsent(event.getContractId(), event);
        }

        if (event instanceof ArchivedEvent) {
            this.getCache().remove(event.getContractId());
        }
    }

}