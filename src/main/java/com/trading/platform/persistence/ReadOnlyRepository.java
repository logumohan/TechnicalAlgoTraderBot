package com.trading.platform.persistence;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import com.trading.platform.persistence.entity.views.InstrumentView;

@NoRepositoryBean
public interface ReadOnlyRepository extends Repository<InstrumentView, String> {

	List<InstrumentView> findOrderedByTickTimeLimitedTo(Class<? extends InstrumentView> clazz, Long token,
			String orderBy, Direction direction, int limit);

	Date getLatestBucketTickTime(Class<? extends InstrumentView> clazz);

	List<Long> getUniqueToken(Class<? extends InstrumentView> clazz);

}
