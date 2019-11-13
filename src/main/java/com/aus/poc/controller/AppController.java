package com.aus.poc.controller;

import java.util.List;

import com.aus.poc.repo.ContractRepo;
import com.daml.ledger.javaapi.data.Event;

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
    private final ContractRepo contractRepo;

    @GetMapping(path = "/{contractId}")
    @ResponseBody
    public Event get(@PathVariable("contractId") String contractId) {
        log.info("Fetching contract for {}", contractId);
        return contractRepo.get(contractId);
    }

    @GetMapping(path = "/")
    @ResponseBody
    public List<Event> getAll() {
        return contractRepo.getAll();
    }
}