package com.firstclass.atm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firstclass.atm.model.Atm;
import com.firstclass.atm.service.AtmService;

@RestController
@RequestMapping("/atm")
public class AtmController {

    private final AtmService atmService;

    public AtmController(AtmService atmService) {
        this.atmService = atmService;
    }

    // Create account
    @PostMapping("/create")
    public ResponseEntity<Atm> createAccount(@RequestBody Atm atm) {
        return new ResponseEntity<>(atmService.createAccount(atm), HttpStatus.CREATED);
    }

    // Check balance
    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<Double> checkBalance(@PathVariable String accountNumber) {
        return ResponseEntity.ok(atmService.checkBalance(accountNumber));
    }

    // Deposit
    @PostMapping("/deposit")
    public ResponseEntity<Atm> deposit(@RequestParam String accountNumber, @RequestParam double amount) {
        return ResponseEntity.ok(atmService.deposit(accountNumber, amount));
    }

    // Withdraw
    @PostMapping("/withdraw")
    public ResponseEntity<Atm> withdraw(@RequestParam String accountNumber, @RequestParam double amount) {
        return ResponseEntity.ok(atmService.withdraw(accountNumber, amount));
    }
}
