package com.project.ewallet.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.ewallet.user.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long>{
	
	Optional<UserInfo> findByEmail(String email);
	

}
