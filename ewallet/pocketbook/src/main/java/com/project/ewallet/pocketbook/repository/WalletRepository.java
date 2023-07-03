package com.project.ewallet.pocketbook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.ewallet.pocketbook.entity.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long>{
	
	Optional<Wallet> findByUserId(Long userId);

}
