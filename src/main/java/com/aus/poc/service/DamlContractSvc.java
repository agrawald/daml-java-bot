package com.aus.poc.service;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.aus.poc.repo.ContractRepo;
import com.daml.ledger.javaapi.data.FiltersByParty;
import com.daml.ledger.javaapi.data.LedgerOffset;
import com.daml.ledger.javaapi.data.NoFilter;
import com.daml.ledger.javaapi.data.TransactionFilter;
import com.daml.ledger.rxjava.DamlLedgerClient;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DamlContractSvc implements InitializingBean {
    @Value("${daml.host}")
    private String host;
    @Value("${daml.port}")
    private int port;
    @Value("${daml.appId}")
    private String appId;
    @Value("${daml.party}")
    private String party;
    @Value("${daml.packageId}")
    private String packageId;

    @Autowired(required = true)
    private ContractRepo contractRepo;

    private DamlLedgerClient client;

    private final static AtomicReference<LedgerOffset> OFFSET = new AtomicReference<>(
            LedgerOffset.LedgerBegin.getInstance());

    @Scheduled(fixedDelay = 5000)
    public void fetch() {
        final TransactionFilter transactionFilter = new FiltersByParty(
                Collections.singletonMap(party, NoFilter.instance));
        client.getTransactionsClient().getTransactions(OFFSET.get(), transactionFilter, true).flatMapIterable(t -> {
            OFFSET.set(new LedgerOffset.Absolute(t.getOffset()));
            return t.getEvents();
        }).forEach(contractRepo);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        client = DamlLedgerClient.forHostWithLedgerIdDiscovery(host, port, Optional.empty());
        client.connect();
    }
}