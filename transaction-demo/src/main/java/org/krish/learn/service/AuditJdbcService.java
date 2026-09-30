package org.krish.learn.service;

import java.math.BigDecimal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditJdbcService {

    private final JdbcTemplate jdbcTemplate;

    public AuditJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(transactionManager = "jdbcTransactionManager", propagation = Propagation.NESTED)
    public void audit(
            String operation,
            String fromAccount,
            String toAccount,
            BigDecimal amount,
            String status) {
        insertAudit(operation, fromAccount, toAccount, amount, status);
    }

    @Transactional(transactionManager = "jdbcTransactionManager", propagation = Propagation.NESTED)
    public void auditAndFail(
            String operation,
            String fromAccount,
            String toAccount,
            BigDecimal amount,
            String status) {
        insertAudit(operation, fromAccount, toAccount, amount, status);
        throw new RuntimeException("Deliberate failure after audit insert");
    }

    private void insertAudit(
            String operation,
            String fromAccount,
            String toAccount,
            BigDecimal amount,
            String status) {
        jdbcTemplate.update(
                "INSERT INTO audit_log (operation, from_account, to_account, amount, status) "
                        + "VALUES (?, ?, ?, ?, ?)",
                operation,
                fromAccount,
                toAccount,
                amount,
                status);
    }
}
