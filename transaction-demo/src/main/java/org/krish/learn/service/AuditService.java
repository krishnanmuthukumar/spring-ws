package org.krish.learn.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    /**
     * Logs an audit entry for a transfer operation.
     *
     * @param operation name of the transfer scenario
     * @param fromAccount account debited by the operation
     * @param toAccount account credited by the operation
     * @param amount transfer amount
     * @param status current operation status
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void audit(String operation, String fromAccount, String toAccount, BigDecimal amount, String status) {
        logger.info(
            "Audit transaction active: {}",
            TransactionSynchronizationManager.isActualTransactionActive());
        logger.info("Audit: operation={}, fromAccount={}, toAccount={}, amount={}, status={}",
                operation, fromAccount, toAccount, amount, status);
    }
}
