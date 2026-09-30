package org.krish.learn.controller;

import java.math.BigDecimal;

import org.krish.learn.service.JdbcTransferService;
import org.krish.learn.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountController {

    private final TransferService transferService;
    private final JdbcTransferService jdbcTransferService;

    public AccountController(TransferService transferService, JdbcTransferService jdbcTransferService) {
        this.transferService = transferService;
        this.jdbcTransferService = jdbcTransferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @RequestParam String fromAccount,
            @RequestParam String toAccount,
            @RequestParam BigDecimal amount) {

        try {
            transferService.transferWithJdbcAudit(fromAccount, toAccount, amount);
            return ResponseEntity.ok("Transfer successful");
//        } catch (InsufficientBalanceException e) {
//            return ResponseEntity.badRequest().body("Insufficient balance");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid account number or request");
        }
    }
    
    @PostMapping("/transferWithJdbc")
    public ResponseEntity<String> transferWithJdbc(
            @RequestParam String fromAccount,
            @RequestParam String toAccount,
            @RequestParam BigDecimal amount) {

        try {
        	jdbcTransferService.transferWithoutCatchingAuditFailure(fromAccount, toAccount, amount);
            return ResponseEntity.ok("Transfer successful");
//        } catch (InsufficientBalanceException e) {
//            return ResponseEntity.badRequest().body("Insufficient balance");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid account number or request");
        }
    }
}
