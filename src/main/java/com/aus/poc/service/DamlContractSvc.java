package com.aus.poc.service;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import com.aus.poc.repo.ContractRepo;
import com.daml.ledger.javaapi.data.FiltersByParty;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.NoFilter;
import com.daml.ledger.javaapi.data.SubmitCommandsRequest;
import com.daml.ledger.javaapi.data.TransactionFilter;
import com.daml.ledger.rxjava.DamlLedgerClient;
import com.daml.ledger.rxjava.components.Bot;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import com.daml.ledger.rxjava.components.helpers.CreatedContract;

import org.pcollections.HashTreePMap;
import org.pcollections.HashTreePSet;
import org.pcollections.PMap;
import org.pcollections.PSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private ContractRepo contractRepo;

    private DamlLedgerClient client;

    // @Scheduled(fixedDelay = 5000)
    public void fetch() {
        final Identifier identifier = new Identifier(packageId, "Iou", "Iou");
        final TransactionFilter transactionFilter = new FiltersByParty(
                Collections.singletonMap(party, NoFilter.instance));
        Bot.wire(appId, client, transactionFilter, (ledgerView) -> {
            PMap<String, CreatedContract> contracts = ledgerView.getContracts(identifier);
            Stream<CommandsAndPendingSet> commandsAndPendingSetStream = contracts.entrySet().stream()
                    .map(this::processContract);
            return Flowable.fromIterable(commandsAndPendingSetStream::iterator);
        }, contractRepo);
    }

    private CommandsAndPendingSet processContract(Map.Entry<String, CreatedContract> entry) {
        final Identifier identifier = new Identifier(packageId, "Iou", "Iou");
        String contractId = entry.getKey();
        CreatedContract contractInfo = entry.getValue();
        String workflowId = contractInfo.getContext().getWorkflowId();
        SubmitCommandsRequest commands = new SubmitCommandsRequest(workflowId, appId,
                UUID.randomUUID().toString(), this.party, Instant.EPOCH, Instant.EPOCH.plusSeconds(10),
                Collections.emptyList());
        PMap<Identifier, PSet<String>> pendingSet = HashTreePMap.singleton(identifier,
                HashTreePSet.singleton(contractId));
        return new CommandsAndPendingSet(commands, pendingSet);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        client = DamlLedgerClient.forHostWithLedgerIdDiscovery(host, port, Optional.empty());
        client.connect();
    }
}