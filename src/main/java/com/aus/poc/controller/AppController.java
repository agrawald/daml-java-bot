package com.aus.poc.controller;

import com.aus.poc.service.ActiveContractSvc;
import com.daml.ledger.rxjava.components.helpers.CreatedContract;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AppController {
    private final ActiveContractSvc activeContractSvc;

    @GetMapping(path = "/{entityName}")
    @ResponseBody
    public CreatedContract get(@PathVariable("entityName") String entityName) {
        log.info("Fetching contract for {}", entityName);
        return activeContractSvc.get(entityName);
    }
}