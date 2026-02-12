package com.firstclass.atm.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.firstclass.atm.model.Atm;

public interface AtmRepository extends JpaRepository<Atm, Long> {

    Optional<Atm> findByAccountNumber(String accountNumber);
}
