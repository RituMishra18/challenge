package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransactionRequest;

import java.math.BigDecimal;

public interface AccountsService {

    void createAccount(Account account);

    Account getAccount(String accountId);

    void transferMoney(TransactionRequest transaction);

    public BigDecimal checkBalance(String accountId);

}
