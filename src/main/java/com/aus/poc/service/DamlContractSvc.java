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
import com.daml.ledger.rxjava.TransactionsClient;

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
    @Value("${daml.party}")
    private String party;

    @Autowired(required = true)
    private ContractRepo contractRepo;

    private DamlLedgerClient client;

    private static LedgerOffset ledgerBegin = LedgerOffset.LedgerBegin.getInstance();
    private static LedgerOffset ledgerEnd;

    @Scheduled(fixedDelay = 5000)
    public void fetch() {
        final TransactionFilter transactionFilter = new FiltersByParty(
                Collections.singletonMap(party, NoFilter.instance));
        final TransactionsClient transactionsClient = client.getTransactionsClient();
        ledgerEnd = transactionsClient.getLedgerEnd().blockingGet();
        log.info("Getting contracts from {} till {}", ledgerBegin, ledgerEnd);
        transactionsClient.getTransactions(ledgerBegin, ledgerEnd, transactionFilter, true).flatMapIterable(t -> t.getEvents())
                .forEach(contractRepo);
        ledgerBegin = ledgerEnd;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        client = DamlLedgerClient.forHostWithLedgerIdDiscovery(host, port, Optional.empty());
        client.connect();
    }
}