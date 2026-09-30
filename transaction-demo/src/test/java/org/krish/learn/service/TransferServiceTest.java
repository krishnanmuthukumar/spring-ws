package org.krish.learn.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.krish.learn.entity.Account;
import org.krish.learn.entity.AuditLog;
import org.krish.learn.exception.InsufficientBalanceException;
import org.krish.learn.repository.AuditLogRepository;
import org.krish.learn.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TransferServiceTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private JdbcTransferService jdbcTransferService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        auditLogRepository.deleteAll();
        accountRepository.save(new Account("ACC-100", "Alice", new BigDecimal("1000.00")));
        accountRepository.save(new Account("ACC-200", "Bob", new BigDecimal("500.00")));
    }

    //@Test
    void transfer_shouldTransferMoney_whenEnoughBalanceExists() throws InsufficientBalanceException {
        transferService.transfer("ACC-100", "ACC-200", new BigDecimal("200.00"));

        Account fromAccount = accountRepository.findByAccountNumber("ACC-100").orElseThrow();
        Account toAccount = accountRepository.findByAccountNumber("ACC-200").orElseThrow();

        assertEquals(new BigDecimal("800.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), toAccount.getBalance());
    }

    @Test
    void transferAndFail_shouldRollbackBothUpdates() {
        assertThrows(RuntimeException.class, () -> transferService.transferAndFail(
                "ACC-100", "ACC-200", new BigDecimal("200.00")));

        Account fromAccount = accountRepository.findByAccountNumber("ACC-100").orElseThrow();
        Account toAccount = accountRepository.findByAccountNumber("ACC-200").orElseThrow();

        assertEquals(new BigDecimal("1000.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("500.00"), toAccount.getBalance());
    }

    @Test
    void transferWithRollbackFor_shouldRollbackBothUpdatesForCheckedException() {
        assertThrows(InsufficientBalanceException.class, () -> transferService.transferWithRollbackFor(
                "ACC-100", "ACC-200", new BigDecimal("200.00")));

        Account fromAccount = accountRepository.findByAccountNumber("ACC-100").orElseThrow();
        Account toAccount = accountRepository.findByAccountNumber("ACC-200").orElseThrow();

        assertEquals(new BigDecimal("1000.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("500.00"), toAccount.getBalance());
    }

    @Test
    void transferWithNoRollbackFor_shouldKeepBothUpdatesForRuntimeException() {
        assertThrows(RuntimeException.class, () -> transferService.transferWithNoRollbackFor(
                "ACC-100", "ACC-200", new BigDecimal("200.00")));

        Account fromAccount = accountRepository.findByAccountNumber("ACC-100").orElseThrow();
        Account toAccount = accountRepository.findByAccountNumber("ACC-200").orElseThrow();

        assertEquals(new BigDecimal("800.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), toAccount.getBalance());
    }

    @Test
    void transferWithRequiredAudit_shouldRollbackTransferAndAuditAfterFailure() {
        assertThrows(RuntimeException.class, () -> transferService.transferWithRequiredAudit(
                "ACC-100", "ACC-200", new BigDecimal("200.00")));

        Account fromAccount = accountRepository.findByAccountNumber("ACC-100").orElseThrow();
        Account toAccount = accountRepository.findByAccountNumber("ACC-200").orElseThrow();

        assertEquals(new BigDecimal("1000.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("500.00"), toAccount.getBalance());

        assertEquals(0L, auditLogRepository.count());
    }

    @Test
    void transferWithRequiresNewAudit_shouldCommitTransferAndRollbackAuditWhenAuditFails() {
        transferService.transferWithRequiresNewAudit("ACC-100", "ACC-200", new BigDecimal("200.00"));

        Account fromAccount = accountRepository.findByAccountNumber("ACC-100").orElseThrow();
        Account toAccount = accountRepository.findByAccountNumber("ACC-200").orElseThrow();

        assertEquals(new BigDecimal("800.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), toAccount.getBalance());
        assertEquals(0L, auditLogRepository.count());
    }

    @Test
    void transferWithJdbcAudit_shouldRollbackTransferWhenNestedAuditFails() {
        assertThrows(RuntimeException.class, () -> transferService.transferWithJdbcAudit(
                "ACC-100", "ACC-200", new BigDecimal("200.00")));

        Account fromAccount = accountRepository.findByAccountNumber("ACC-100").orElseThrow();
        Account toAccount = accountRepository.findByAccountNumber("ACC-200").orElseThrow();

        assertEquals(new BigDecimal("1000.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("500.00"), toAccount.getBalance());
        assertEquals(0L, auditLogRepository.count());
    }

    @Test
    void jdbcTransfer_shouldCommitAccountChangesAndRollbackNestedAudit() {
        jdbcTransferService.transfer("ACC-100", "ACC-200", new BigDecimal("200.00"));

        Account fromAccount = accountRepository.findByAccountNumber("ACC-100").orElseThrow();
        Account toAccount = accountRepository.findByAccountNumber("ACC-200").orElseThrow();

        assertEquals(new BigDecimal("800.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("700.00"), toAccount.getBalance());
        assertEquals(0L, auditLogRepository.count());
    }

    @Test
    void jdbcTransferWithoutCatchingAuditFailure_shouldRollbackTransferAndNestedAudit() {
        assertThrows(RuntimeException.class, () -> jdbcTransferService.transferWithoutCatchingAuditFailure(
                "ACC-100", "ACC-200", new BigDecimal("200.00")));

        Account fromAccount = accountRepository.findByAccountNumber("ACC-100").orElseThrow();
        Account toAccount = accountRepository.findByAccountNumber("ACC-200").orElseThrow();

        assertEquals(new BigDecimal("1000.00"), fromAccount.getBalance());
        assertEquals(new BigDecimal("500.00"), toAccount.getBalance());
        assertEquals(0L, auditLogRepository.count());
    }
}
