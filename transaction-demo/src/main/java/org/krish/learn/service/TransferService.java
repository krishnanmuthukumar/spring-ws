package org.krish.learn.service;

import java.math.BigDecimal;

import org.krish.learn.entity.Account;
import org.krish.learn.exception.InsufficientBalanceException;
import org.krish.learn.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class TransferService {

	private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

	private final AccountRepository accountRepository;
	private final AuditService auditService;

	public TransferService(AccountRepository accountRepository, AuditService auditService) {
		this.accountRepository = accountRepository;
		this.auditService = auditService;
	}

	/**
	 * Demonstrates the default Spring behavior for a checked exception: the account
	 * updates are not rolled back unless rollbackFor is configured.
	 *
	 * @param fromAccount account to debit
	 * @param toAccount account to credit
	 * @param amount amount to transfer
	 * @throws InsufficientBalanceException deliberately after both updates
	 */
	@Transactional
	public void transfer(String fromAccount, String toAccount, BigDecimal amount) throws InsufficientBalanceException {

		logger.info("Starting transfer from {} to {} for amount {}", fromAccount, toAccount, amount);

		Account from = accountRepository.findByAccountNumber(fromAccount).orElseThrow();

		Account to = accountRepository.findByAccountNumber(toAccount).orElseThrow();

//        if (from.getBalance().compareTo(amount) < 0) {
//            logger.warn("Transfer rejected due to insufficient balance in account {}", fromAccount);
//            throw new InsufficientBalanceException();
//        }

		from.setBalance(from.getBalance().subtract(amount));
		accountRepository.save(from);

		to.setBalance(to.getBalance().add(amount));

		accountRepository.save(to);
		logger.info("Transfer completed from {} to {} for amount {}", fromAccount, toAccount, amount);

		// Deliberately throw checked exception
		throw new InsufficientBalanceException();
	}

	/**
	 * Demonstrates rollbackFor by rolling back both updates when the checked
	 * InsufficientBalanceException is thrown after the updates.
	 *
	 * @param fromAccount account to debit
	 * @param toAccount account to credit
	 * @param amount amount to transfer
	 * @throws InsufficientBalanceException deliberately to trigger rollback
	 */
	@Transactional(rollbackFor = InsufficientBalanceException.class)
	public void transferWithRollbackFor(String fromAccount, String toAccount, BigDecimal amount)
			throws InsufficientBalanceException {

		logger.info("Starting rollbackFor transfer from {} to {} for amount {}", fromAccount, toAccount, amount);

		Account from = accountRepository.findByAccountNumber(fromAccount).orElseThrow();
		Account to = accountRepository.findByAccountNumber(toAccount).orElseThrow();

		from.setBalance(from.getBalance().subtract(amount));
		accountRepository.save(from);

		to.setBalance(to.getBalance().add(amount));
		accountRepository.save(to);

		logger.warn("Throwing checked exception to trigger rollback for transfer from {} to {}", fromAccount, toAccount);
		throw new InsufficientBalanceException();
	}

	/**
	 * Demonstrates noRollbackFor by keeping both updates committed even though a
	 * RuntimeException is thrown after the updates.
	 *
	 * @param fromAccount account to debit
	 * @param toAccount account to credit
	 * @param amount amount to transfer
	 * @throws RuntimeException deliberately after both updates
	 */
	@Transactional(noRollbackFor = RuntimeException.class)
	public void transferWithNoRollbackFor(String fromAccount, String toAccount, BigDecimal amount) {

		logger.info("Starting noRollbackFor transfer from {} to {} for amount {}", fromAccount, toAccount, amount);

		Account from = accountRepository.findByAccountNumber(fromAccount).orElseThrow();
		Account to = accountRepository.findByAccountNumber(toAccount).orElseThrow();

		from.setBalance(from.getBalance().subtract(amount));
		accountRepository.save(from);

		to.setBalance(to.getBalance().add(amount));
		accountRepository.save(to);

		logger.warn("Throwing runtime exception without rollback for transfer from {} to {}", fromAccount, toAccount);
		throw new RuntimeException("Something went wrong");
	}

	/**
	 * Demonstrates the default Spring behavior for a runtime exception: both
	 * account updates are rolled back when the operation fails.
	 *
	 * @param fromAccount account to debit
	 * @param toAccount account to credit
	 * @param amount amount to transfer
	 * @throws InsufficientBalanceException if the source account lacks funds
	 * @throws RuntimeException deliberately after both updates
	 */
	@Transactional
	public void transferAndFail(String fromAccount, String toAccount, BigDecimal amount)
			throws InsufficientBalanceException {

		logger.info("Starting failing transfer from {} to {} for amount {}", fromAccount, toAccount, amount);

		Account from = accountRepository.findByAccountNumber(fromAccount).orElseThrow();

		Account to = accountRepository.findByAccountNumber(toAccount).orElseThrow();

		if (from.getBalance().compareTo(amount) < 0) {
			logger.warn("Failing transfer rejected due to insufficient balance in account {}", fromAccount);
			throw new InsufficientBalanceException();
		}

		from.setBalance(from.getBalance().subtract(amount));
		accountRepository.save(from);

		to.setBalance(to.getBalance().add(amount));
		accountRepository.save(to);

		logger.error("Forcing failure after updating accounts {} and {}", fromAccount, toAccount);
		throw new RuntimeException("Something went wrong");
	}

	/**
	 * Demonstrates REQUIRED propagation by calling the audit service from the
	 * transfer transaction. The audit call joins this transaction, and the forced
	 * failure rolls back the account updates as one unit.
	 *
	 * @param fromAccount account to debit
	 * @param toAccount account to credit
	 * @param amount amount to transfer
	 * @throws RuntimeException deliberately after the audit call
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void transferWithRequiredAudit(String fromAccount, String toAccount, BigDecimal amount) {

		logger.info("Starting REQUIRED transfer from {} to {} for amount {}", fromAccount, toAccount, amount);

		Account from = accountRepository.findByAccountNumber(fromAccount).orElseThrow();
		Account to = accountRepository.findByAccountNumber(toAccount).orElseThrow();

		from.setBalance(from.getBalance().subtract(amount));
		accountRepository.save(from);

		to.setBalance(to.getBalance().add(amount));
		accountRepository.save(to);

		logger.info(
				"Transfer transaction active: {}",
				TransactionSynchronizationManager.isActualTransactionActive());
		auditService.audit("transferWithRequiredAudit", fromAccount, toAccount, amount, "UPDATED");

		throw new RuntimeException("Something went wrong after audit");
	}
}