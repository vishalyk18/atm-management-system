package com.firstclass.atm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firstclass.atm.exception.AccountNotFoundException;
import com.firstclass.atm.exception.InsufficientBalanceException;
import com.firstclass.atm.model.Atm;
import com.firstclass.atm.repository.AtmRepository;

@Service
public class AtmServiceImpl implements AtmService {

    @Autowired
    private AtmRepository atmRepository;

    @Override
    public Atm createAccount(Atm atm) {
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
        return atmRepository.save(atm);
    }

    @Override
    public Atm withdraw(String accountNumber, double amount) {
        Atm atm = atmRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (atm.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        atm.setBalance(atm.getBalance() - amount);
        return atmRepository.save(atm);
    }
}
