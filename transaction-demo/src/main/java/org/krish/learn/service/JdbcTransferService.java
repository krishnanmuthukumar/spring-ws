package org.krish.learn.service;

import java.math.BigDecimal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JdbcTransferService {

    private final JdbcTemplate jdbcTemplate;
    private final AuditJdbcService auditJdbcService;

    public JdbcTransferService(JdbcTemplate jdbcTemplate, AuditJdbcService auditJdbcService) {
        this.jdbcTemplate = jdbcTemplate;
        this.auditJdbcService = auditJdbcService;
    }

    @Transactional(transactionManager = "jdbcTransactionManager", propagation = Propagation.REQUIRED)
    public void transfer(String fromAccount, String toAccount, BigDecimal amount) {
        updateBalances(fromAccount, toAccount, amount);

        try {
            auditJdbcService.auditAndFail("jdbcTransfer", fromAccount, toAccount, amount, "UPDATED");
        } catch (RuntimeException ex) {
            // The nested audit rolls back to its savepoint; the transfer can still commit.
        }
    }

    @Transactional(transactionManager = "jdbcTransactionManager", propagation = Propagation.REQUIRED)
    public void transferWithoutCatchingAuditFailure(String fromAccount, String toAccount, BigDecimal amount) {
        updateBalances(fromAccount, toAccount, amount);
        auditJdbcService.auditAndFail("jdbcTransferWithoutCatch", fromAccount, toAccount, amount, "UPDATED");
    }

    private void updateBalances(String fromAccount, String toAccount, BigDecimal amount) {
        if (fromAccount.equals(toAccount)) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        String firstAccount = fromAccount.compareTo(toAccount) < 0 ? fromAccount : toAccount;
        String secondAccount = fromAccount.compareTo(toAccount) < 0 ? toAccount : fromAccount;
        BigDecimal firstBalance = lockAndReadBalance(firstAccount);
        BigDecimal secondBalance = lockAndReadBalance(secondAccount);

        BigDecimal fromBalance = fromAccount.equals(firstAccount) ? firstBalance : secondBalance;
        BigDecimal toBalance = toAccount.equals(firstAccount) ? firstBalance : secondBalance;
        if (fromBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance in source account");
        }

        jdbcTemplate.update("UPDATE account SET balance = ? WHERE account_number = ?",
                fromBalance.subtract(amount), fromAccount);
        jdbcTemplate.update("UPDATE account SET balance = ? WHERE account_number = ?",
                toBalance.add(amount), toAccount);
    }

    private BigDecimal lockAndReadBalance(String accountNumber) {
        return jdbcTemplate.queryForObject(
                "SELECT balance FROM account WHERE account_number = ? FOR UPDATE",
                BigDecimal.class,
                accountNumber);
    }
}