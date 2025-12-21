package com.trading.platform.persistence;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import com.trading.platform.persistence.entity.InstrumentSubscription;

@NoRepositoryBean
public interface SubscriptionReadOnlyRepositoryIf extends Repository<InstrumentSubscription, String> {

	List<InstrumentSubscription> getAll();
	
	List<Long> getAllTokens();

	InstrumentSubscription getByToken(Long token);

}
