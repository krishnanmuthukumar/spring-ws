error id: file:///C:/Krishnan/Java/spring-ws/transaction-demo/src/main/java/org/krish/learn/repository/AccountRepository.java:java/util/Optional#
file:///C:/Krishnan/Java/spring-ws/transaction-demo/src/main/java/org/krish/learn/repository/AccountRepository.java
empty definition using pc, found symbol in pc: java/util/Optional#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 56
uri: file:///C:/Krishnan/Java/spring-ws/transaction-demo/src/main/java/org/krish/learn/repository/AccountRepository.java
text:
```scala
package org.krish.learn.repository;

import java.util.@@Optional;

import org.krish.learn.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/Optional#