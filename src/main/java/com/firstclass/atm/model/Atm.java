package com.firstclass.atm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Atm {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId;

	@Column(unique = true, nullable = false)
	private String accountNumber;

	@Column(nullable = false)
	private String pin;

	@Column(nullable = false)
	private double balance;

	public Atm() {
	}

	public Atm(Long accountId, String accountNumber, String pin, double balance) {
		this.accountId = accountId;
		this.accountNumber = accountNumber;
		this.pin = pin;
		this.balance = balance;
	}

	public Atm(String accountNumber, String pin, double balance) {
		this.accountNumber = accountNumber;
		this.pin = pin;
		this.balance = balance;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
}
