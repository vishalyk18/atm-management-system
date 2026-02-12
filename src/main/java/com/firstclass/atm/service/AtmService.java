package com.firstclass.atm.service;

import com.firstclass.atm.model.Atm;

public interface AtmService {
    Atm createAccount(Atm atm);

    double checkBalance(String accountNumber);

    Atm deposit(String accountNumber, double amount);

    Atm withdraw(String accountNumber, double amount);
}
