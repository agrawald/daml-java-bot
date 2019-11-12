package com.aus.poc.service;

import java.util.Collections;
import java.util.Optional;

import com.aus.poc.repo.ContractCache;
import com.daml.ledger.javaapi.data.FiltersByParty;
import com.daml.ledger.javaapi.data.NoFilter;
import com.daml.ledger.javaapi.data.TransactionFilter;
import com.daml.ledger.rxjava.DamlLedgerClient;
import com.daml.ledger.rxjava.components.Bot;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.reactivex.Flowable;
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
    private ContractCache contractCache;

    private DamlLedgerClient client;

    @Scheduled(fixedDelay = 5000)
    public void fetch() {
        final TransactionFilter transactionFilter = new FiltersByParty(
                Collections.singletonMap(party, NoFilter.instance));
        Bot.wire(appId, client, transactionFilter, (ledgerView) -> Flowable.empty(),
            contractCache);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        client = DamlLedgerClient.forHostWithLedgerIdDiscovery(host, port, Optional.empty());
        client.connect();
    }
}