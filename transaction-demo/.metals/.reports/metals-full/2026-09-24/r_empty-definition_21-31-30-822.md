error id: file:///C:/Krishnan/Java/spring-ws/transaction-demo/src/main/java/org/krish/learn/service/TransferService.java:_empty_/AccountRepository#findByAccountNumber#orElseThrow#
file:///C:/Krishnan/Java/spring-ws/transaction-demo/src/main/java/org/krish/learn/service/TransferService.java
empty definition using pc, found symbol in pc: _empty_/AccountRepository#findByAccountNumber#orElseThrow#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 809
uri: file:///C:/Krishnan/Java/spring-ws/transaction-demo/src/main/java/org/krish/learn/service/TransferService.java
text:
```scala
package org.krish.learn.service;

import java.math.BigDecimal;

import org.krish.learn.entity.Account;
import org.krish.learn.exception.InsufficientBalanceException;
import org.krish.learn.repository.AccountRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class TransferService {

    private final AccountRepository accountRepository;

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void transfer(
            String fromAccount,
            String toAccount,
            BigDecimal amount) {

        Account from = accountRepository
                .findByAccountNumber(fromAccount)
                .@@orElseThrow();

        Account to = accountRepository
                .findByAccountNumber(toAccount)
                .orElseThrow();

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        from.setBalance(from.getBalance().subtract(amount));

        to.setBalance(to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/AccountRepository#findByAccountNumber#orElseThrow#