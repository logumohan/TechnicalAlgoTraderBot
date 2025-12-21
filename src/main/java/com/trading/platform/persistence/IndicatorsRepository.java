package com.trading.platform.persistence;

import java.util.List;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import com.trading.platform.persistence.entity.InstrumentIndicators;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
@SuppressWarnings("unchecked")
public class IndicatorsRepository implements IndicatorsReadOnlyRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<InstrumentIndicators> findOrderedByTickTimeLimitedTo(Class<? extends InstrumentIndicators> clazz,
			Long token, String orderBy, Direction direction, int limit) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<InstrumentIndicators> limitQuery = (CriteriaQuery<InstrumentIndicators>) cb.createQuery(clazz);
		Root<InstrumentIndicators> from = (Root<InstrumentIndicators>) limitQuery.from(clazz);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get("token"), token));
		if (Direction.ASC.equals(direction)) {
			limitQuery.orderBy(cb.asc(from.get(orderBy)));
		} else {
			limitQuery.orderBy(cb.desc(from.get(orderBy)));
		}

		return entityManager.createQuery(limitQuery).setMaxResults(limit).getResultList();
	}

	@Override
	public InstrumentIndicators findLast(Class<? extends InstrumentIndicators> clazz, Long token) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<InstrumentIndicators> limitQuery = (CriteriaQuery<InstrumentIndicators>) cb.createQuery(clazz);
		Root<InstrumentIndicators> from = (Root<InstrumentIndicators>) limitQuery.from(clazz);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get("token"), token));
		limitQuery.orderBy(cb.desc(from.get("tickTime")));

		List<InstrumentIndicators> indicatorList = entityManager.createQuery(limitQuery).setMaxResults(1)
				.getResultList();

		return indicatorList.isEmpty() ? null : indicatorList.get(0);
	}

}
