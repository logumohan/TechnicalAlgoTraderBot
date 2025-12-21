package com.trading.platform.persistence;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import com.trading.platform.LogExecutionTime;
import com.trading.platform.persistence.entity.views.InstrumentView;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
@SuppressWarnings("unchecked")
public class InstrumentsViewRepository implements ReadOnlyRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@LogExecutionTime
	public List<InstrumentView> findOrderedByTickTimeLimitedTo(Class<? extends InstrumentView> clazz, Long token,
			String orderBy, Direction direction, int limit) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<InstrumentView> limitQuery = (CriteriaQuery<InstrumentView>) cb.createQuery(clazz);
		Root<InstrumentView> from = (Root<InstrumentView>) limitQuery.from(clazz);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get("token"), token));
		limitQuery.orderBy(cb.desc(from.get(orderBy)));

		return entityManager.createQuery(limitQuery).setMaxResults(limit).getResultList();
	}

	@Override
	@LogExecutionTime
	public Date getLatestBucketTickTime(Class<? extends InstrumentView> clazz) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Date> limitQuery = cb.createQuery(Date.class);
		Root<InstrumentView> from = (Root<InstrumentView>) limitQuery.from(clazz);
		limitQuery.select(from.get("bucketTickTime"));
		limitQuery.distinct(true);
		limitQuery.orderBy(cb.desc(from.get("bucketTickTime")));

		List<Date> list = entityManager.createQuery(limitQuery).setMaxResults(2).getResultList();
		return (list.size() > 1) ? list.get(1) : null;
	}

	@Override
	@LogExecutionTime
	public List<Long> getUniqueToken(Class<? extends InstrumentView> clazz) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> limitQuery = cb.createQuery(Long.class);
		Root<InstrumentView> from = (Root<InstrumentView>) limitQuery.from(clazz);
		limitQuery.select(from.get("token"));
		limitQuery.distinct(true);

		return entityManager.createQuery(limitQuery).getResultList();
	}

}
