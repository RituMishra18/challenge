package com.dws.challenge;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.TransactionRequest;
import com.dws.challenge.exception.AccountNotExistException;
import com.dws.challenge.exception.OverDraftException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.EmailNotificationService;
import com.dws.challenge.service.impl.AccountsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {
    @InjectMocks
    private AccountsServiceImpl accountsService;

    @Mock
    AccountsRepository accountsRepository;

    @Mock
    EmailNotificationService notificationService;


    @Test
    void addAccount() {
        Account account = new Account("Id-123");
        account.setBalance(new BigDecimal(1000));
        this.accountsService.createAccount(account);
    }

    @Test
    void getAccount() {
        Account account = new Account("Id-123");
        account.setBalance(new BigDecimal(1000));
        when(accountsRepository.getAccount("Id-123")).thenReturn(account);
        assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
    }

    @Test
    void testRetrieveBalance() {
        when(this.accountsService.getAccount("1234")).thenReturn(new Account("1234", BigDecimal.ONE));
        assertThat(accountsService.getAccount("1234").getBalance()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    void testTransferBalance() {
        String accountFromId = "1234";
        String accountToId = "2345";
        BigDecimal amount = new BigDecimal(10);

        TransactionRequest request = new TransactionRequest();
        request.setAccountFrom(accountFromId);
        request.setAccountTo(accountToId);
        request.setAmount(amount);

        Account accFrom = new Account(accountFromId, BigDecimal.TEN);
        Account accTo = new Account(accountFromId, BigDecimal.TEN);

        when(accountsRepository.getAccount(accountFromId)).thenReturn(accFrom);
        when(accountsRepository.getAccount(accountToId)).thenReturn(accTo);

        doNothing().when(notificationService).notifyAboutTransfer(Mockito.any(), Mockito.anyString());
        accountsService.transferMoney(request);

        assertEquals(BigDecimal.ZERO, accFrom.getBalance());
        assertEquals(BigDecimal.TEN.add(BigDecimal.TEN), accTo.getBalance());
    }

    @Test()
    void testOverdraftBalance() throws OverDraftException, AccountNotExistException {
        String accountFromId = "1234";
        String accountToId = "2345";
        BigDecimal amount = new BigDecimal(20);

        TransactionRequest request = new TransactionRequest();
        request.setAccountFrom(accountFromId);
        request.setAccountTo(accountToId);
        request.setAmount(amount);

        Account accFrom = new Account(accountFromId, BigDecimal.TEN);
        Account accTo = new Account(accountFromId, BigDecimal.TEN);
        when(accountsRepository.getAccount(accountFromId)).thenReturn(accFrom);
        when(accountsRepository.getAccount(accountToId)).thenReturn(accTo);

        Exception exception = assertThrows(OverDraftException.class, () -> {
            accountsService.transferMoney(request);
        });
        String expectedMessage = "Account with id:1234 does not have enough balance to transfer.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testTransferBalanceFromInvalidFromAccount() {
        String accountFromId = "1234";
        String accountToId = "2345";
        BigDecimal amount = new BigDecimal(20);

        TransactionRequest request = new TransactionRequest();
        request.setAccountFrom(accountFromId);
        request.setAccountTo(accountToId);
        request.setAmount(amount);

        when(accountsRepository.getAccount("1234")).thenReturn(null);
        Exception exception = assertThrows(AccountNotExistException.class, () -> {
            accountsService.transferMoney(request);
        });
        String expectedMessage = "Account does not exist.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testTransferBalanceFromInvalidToAccount() {
        String accountFromId = "1234";
        String accountToId = "2345";
        BigDecimal amount = new BigDecimal(20);

        TransactionRequest request = new TransactionRequest();
        request.setAccountFrom(accountFromId);
        request.setAccountTo(accountToId);
        request.setAmount(amount);

        when(accountsRepository.getAccount("2345")).thenReturn(null);

        Exception exception = assertThrows(AccountNotExistException.class, () -> {
            accountsService.transferMoney(request);
        });
        String expectedMessage = "Account does not exist.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}
