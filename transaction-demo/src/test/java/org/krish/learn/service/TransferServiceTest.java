package org.krish.learn.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.krish.learn.entity.Account;
import org.krish.learn.exception.InsufficientBalanceException;
import org.krish.learn.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TransferServiceTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        accountRepository.save(new Account("ACC-100", "Alice", new BigDecimal("1000.00")));
        accountRepository.save(new Account("ACC-200", "Bob", new BigDecimal("500.00")));
    }

    @Test
    void transfer_shouldTransferMoney_whenEnoughBalanceExists() throws InsufficientBalanceException {
        transferService.transfer("ACC-100", "ACC-200", new BigDecimal("200.00"));

        Account fromAccount = accountRepository.findByAccountNumber("ACC-100").orElseThrow();
        Account toAccount = accountRepository.findByAccountNumber("ACC-200").orElseThrow();

        assertEquals(new BigDecimal("800.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), toAccount.getBalance());
    }
}
