package com.microservice.h2db.Repository;

import com.microservice.h2db.model.Wallet;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface WalletRepository extends Repository<Wallet, Integer> {

    Wallet save(Wallet wallet);
    
    Wallet findById(Integer id);

    Wallet findByUserId(Integer userId);
    
    List<Wallet> findAll();
}

