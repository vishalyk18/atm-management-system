package com.firstclass.atm.security;

import com.firstclass.atm.model.Atm;
import com.firstclass.atm.repository.AtmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AtmRepository atmRepository;

    @Override
    public UserDetails loadUserByUsername(String accountNumber) throws UsernameNotFoundException {
        Atm atm = atmRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Account Not Found with account number: " + accountNumber));

        return new User(atm.getAccountNumber(), atm.getPin(), new ArrayList<>());
    }
}
