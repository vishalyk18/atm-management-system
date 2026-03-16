package com.firstclass.atm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstclass.atm.exception.AccountNotFoundException;
import com.firstclass.atm.exception.InsufficientBalanceException;
import com.firstclass.atm.model.Atm;
import com.firstclass.atm.model.Transaction;
import com.firstclass.atm.repository.AtmRepository;
import com.firstclass.atm.repository.TransactionRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AtmServiceImpl implements AtmService {

    @Autowired
    private AtmRepository atmRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Atm createAccount(Atm atm) {
        atm.setPin(passwordEncoder.encode(atm.getPin()));
        return atmRepository.save(atm);
    }

    @Override
    public double checkBalance(String accountNumber) {
        Atm atm = atmRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return atm.getBalance();
    }

    @Override
    public Atm deposit(String accountNumber, double amount) {
        Atm atm = atmRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        atm.setBalance(atm.getBalance() + amount);
        Atm updatedAtm = atmRepository.save(atm);

        // Record transaction
        Transaction transaction = new Transaction(accountNumber, "DEPOSIT", amount, LocalDateTime.now());
        transactionRepository.save(transaction);

        return updatedAtm;
    }

    @Override
    public Atm withdraw(String accountNumber, double amount) {
        Atm atm = atmRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (atm.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        atm.setBalance(atm.getBalance() - amount);
        Atm updatedAtm = atmRepository.save(atm);

        // Record transaction
        Transaction transaction = new Transaction(accountNumber, "WITHDRAW", amount, LocalDateTime.now());
        transactionRepository.save(transaction);

        return updatedAtm;
    }

    @Override
    public List<Transaction> getTransactionHistory(String accountNumber) {
        // Verify account exists
        atmRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        return transactionRepository.findByAccountNumberOrderByTimestampDesc(accountNumber);
    }
}
