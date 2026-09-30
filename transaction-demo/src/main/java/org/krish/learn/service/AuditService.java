package org.krish.learn.service;

import java.math.BigDecimal;

import org.krish.learn.entity.AuditLog;
import org.krish.learn.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);
    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
    * Saves an audit entry in the caller's transaction, or starts a transaction if needed.
     *
     * @param operation name of the transfer scenario
     * @param fromAccount account debited by the operation
     * @param toAccount account credited by the operation
     * @param amount transfer amount
     * @param status current operation status
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void audit(String operation, String fromAccount, String toAccount, BigDecimal amount, String status) {
        saveAudit(operation, fromAccount, toAccount, amount, status);
        
    }

    /**
    * Saves an audit entry in a transaction independent of the caller's transaction.
    * This demonstration method then throws, causing its new transaction to roll back.
     *
     * @param operation name of the transfer scenario
     * @param fromAccount account debited by the operation
     * @param toAccount account credited by the operation
     * @param amount transfer amount
     * @param status current operation status
    * @throws RuntimeException deliberately after saving the audit entry
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditWithRequiresNew(String operation, String fromAccount, String toAccount, BigDecimal amount,
            String status) {
        saveAudit(operation, fromAccount, toAccount, amount, status);
        throw new RuntimeException("Something went in audit");
    }

    private void saveAudit(String operation, String fromAccount, String toAccount, BigDecimal amount, String status) {
        logger.info(
            "Audit transaction active: {}",
            TransactionSynchronizationManager.isActualTransactionActive());
        logger.info("Audit: operation={}, fromAccount={}, toAccount={}, amount={}, status={}",
                operation, fromAccount, toAccount, amount, status);
        auditLogRepository.save(new AuditLog(operation, fromAccount, toAccount, amount, status));
    }
}
