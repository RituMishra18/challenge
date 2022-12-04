package com.dws.challenge.web;

import com.dws.challenge.constant.ErrorCode;
import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.APIException;
import com.dws.challenge.exception.AccountNotExistException;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.AccountsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {
    private final AccountsService accountsService;

    @Autowired
    public AccountsController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createAccount(@Valid @RequestBody Account account) {
        log.info("Creating account {}", account);
        try {
            this.accountsService.createAccount(account);
        } catch (DuplicateAccountIdException daie) {
            return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAccount(@PathVariable String accountId) {
        log.info("Retrieving account for id {}", accountId);
        Account account;
        try {
            account = this.accountsService.getAccount(accountId);
        } catch (AccountNotExistException ex) {
            log.error("Account doesn't exists.");
            return new ResponseEntity<>(ex.getErrorCode(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(account, HttpStatus.OK);
    }
}
