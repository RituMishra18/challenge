package com.dws.challenge.service.impl;

import com.dws.challenge.constant.ErrorCode;
import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransactionRequest;
import com.dws.challenge.exception.AccountNotExistException;
import com.dws.challenge.exception.CheckBalanceException;
import com.dws.challenge.exception.OverDraftException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.NotificationService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountsServiceImpl implements AccountsService {

    @Getter
    private final AccountsRepository accountsRepository;

    private NotificationService notificationService;

    @Autowired
    public AccountsServiceImpl(AccountsRepository accountsRepository,
                               NotificationService notificationService) {
        this.accountsRepository = accountsRepository;
        this.notificationService = notificationService;
    }

    public void createAccount(Account account) {
        this.accountsRepository.createAccount(account);
    }

    public Account getAccount(String accountId) {
        return this.accountsRepository.getAccount(accountId);
    }

    @Override
    public synchronized void transferMoney(TransactionRequest transaction) {
        Account accountFrom = accountsRepository.getAccount(transaction.getAccountFrom());
        Account accountTo = accountsRepository.getAccount(transaction.getAccountTo());
        if (accountFrom != null && accountTo != null) {
            if (accountFrom.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new OverDraftException("Account with id:" + accountFrom.getAccountId() +
                        " does not have enough balance to transfer.", ErrorCode.INSUFFICIENT_ACCOUNT_BALANCE);
            }
            accountFrom.setBalance(accountFrom.getBalance().subtract(transaction.getAmount()));
            accountTo.setBalance(accountTo.getBalance().add(transaction.getAmount()));
            notificationService.notifyAboutTransfer(accountFrom, "Amount " + transaction.getAmount() + " transferred to" + accountTo.getAccountId());
            notificationService.notifyAboutTransfer(accountTo, "Amount " + transaction.getAmount() + " received from" + accountFrom.getAccountId());
        } else {
            throw new AccountNotExistException("Account does not exist.", ErrorCode.ACCOUNT_ERROR,
                    HttpStatus.BAD_REQUEST);
        }
    }

    public BigDecimal checkBalance(String accountId) {
        BigDecimal balance = null;
        try {
            Account account = accountsRepository.getAccount(accountId);
            if (account != null) {
                balance = account.getBalance();
            }
        } catch (CheckBalanceException ex) {
            throw new CheckBalanceException("Exception occurred while retrieving the account balance", ErrorCode.ACCOUNT_BALANCE_ERROR);
        }
        return balance;
    }
}
