package com.firstclass.atm.service;

import com.firstclass.atm.model.Atm;
import com.firstclass.atm.model.Transaction;
import java.util.List;

public interface AtmService {
    Atm createAccount(Atm atm);

    double checkBalance(String accountNumber);

    Atm deposit(String accountNumber, double amount);

    Atm withdraw(String accountNumber, double amount);

    List<Transaction> getTransactionHistory(String accountNumber);
}
