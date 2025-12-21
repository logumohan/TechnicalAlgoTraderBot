package com.trading.platform.persistence;

import java.util.List;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import com.trading.platform.persistence.entity.InstrumentIndicators;

@NoRepositoryBean
public interface IndicatorsReadOnlyRepository extends Repository<InstrumentIndicators, String> {

	List<InstrumentIndicators> findOrderedByTickTimeLimitedTo(Class<? extends InstrumentIndicators> clazz,
			Long token, String orderBy, Direction direction, int limit);

	InstrumentIndicators findLast(Class<? extends InstrumentIndicators> clazz, Long token);

}
