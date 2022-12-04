package com.dws.challenge.web;

import com.dws.challenge.domain.TransactionRequest;
import com.dws.challenge.domain.dto.TransferResult;
import com.dws.challenge.exception.AccountNotExistException;
import com.dws.challenge.exception.CheckBalanceException;
import com.dws.challenge.exception.OverDraftException;
import com.dws.challenge.service.AccountsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1/transaction")
public class TransactionController {

    private final AccountsService accountsService;

    @Autowired
    public TransactionController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> transferMoney(@RequestBody @Valid TransactionRequest request) throws Exception {

        try {
            accountsService.transferMoney(request);
            TransferResult result = new TransferResult();
            result.setAccountFromId(request.getAccountFrom());
            result.setBalanceAfterTransfer(accountsService.checkBalance(request.getAccountFrom()));
            return new ResponseEntity<>(result, HttpStatus.ACCEPTED);
        } catch (AccountNotExistException | OverDraftException e) {
            log.error("Failed to transfer balances.");
            return new ResponseEntity<>(e.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (CheckBalanceException ex) {
            log.error("Failed to check balances after transfer.");
            throw ex;
        }
    }
}
