package org.krish.learn.service;

import java.math.BigDecimal;

import org.krish.learn.entity.Account;
import org.krish.learn.exception.InsufficientBalanceException;
import org.krish.learn.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class TransferService {

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    //@Transactional
    public void transfer(
            String fromAccount,
            String toAccount,
            BigDecimal amount) throws InsufficientBalanceException {

        Account from = accountRepository
                .findByAccountNumber(fromAccount)
                .orElseThrow();

        Account to = accountRepository
                .findByAccountNumber(toAccount)
                .orElseThrow();

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        from.setBalance(from.getBalance().subtract(amount));
        Account a = accountRepository.save(from);
        if(a != null)
        	throw new RuntimeException("Something went wrong");

        to.setBalance(to.getBalance().add(amount));

    
        
        accountRepository.save(to);
    }
}