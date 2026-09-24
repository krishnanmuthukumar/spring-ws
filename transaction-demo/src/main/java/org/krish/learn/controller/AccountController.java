package org.krish.learn.controller;

import java.math.BigDecimal;

import org.krish.learn.exception.InsufficientBalanceException;
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

    public AccountController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @RequestParam String fromAccount,
            @RequestParam String toAccount,
            @RequestParam BigDecimal amount) {

        try {
            transferService.transfer(fromAccount, toAccount, amount);
            return ResponseEntity.ok("Transfer successful");
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.badRequest().body("Insufficient balance");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid account number or request");
        }
    }
}
