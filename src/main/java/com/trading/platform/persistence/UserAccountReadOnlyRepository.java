package com.trading.platform.persistence;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import com.trading.platform.persistence.entity.UserAccount;

@NoRepositoryBean
public interface UserAccountReadOnlyRepository extends Repository<UserAccount, String> {

	UserAccount findByClientId(String clientId);
	
	UserAccount getMasterAccount();
	
	List<UserAccount> getAllUserAccount();
	
	List<UserAccount> getAllTradableUserAccount();

}
