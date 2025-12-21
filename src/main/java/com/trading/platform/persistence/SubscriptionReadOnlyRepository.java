package com.trading.platform.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.trading.platform.persistence.entity.InstrumentSubscription;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
public class SubscriptionReadOnlyRepository implements SubscriptionReadOnlyRepositoryIf {

	private static final String TOKEN_COL_NAME = "token";

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<InstrumentSubscription> getAll() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<InstrumentSubscription> limitQuery = cb.createQuery(InstrumentSubscription.class);
		Root<InstrumentSubscription> from = limitQuery.from(InstrumentSubscription.class);
		limitQuery.select(from);

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Long> getAllTokens() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> limitQuery = cb.createQuery(Long.class);
		Root<InstrumentSubscription> from = limitQuery.from(InstrumentSubscription.class);
		limitQuery.select(from.get(TOKEN_COL_NAME));
		limitQuery.distinct(true);

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public InstrumentSubscription getByToken(Long token) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<InstrumentSubscription> limitQuery = cb.createQuery(InstrumentSubscription.class);
		Root<InstrumentSubscription> from = limitQuery.from(InstrumentSubscription.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(TOKEN_COL_NAME), token));

		List<InstrumentSubscription> subscriptionList = entityManager.createQuery(limitQuery).getResultList();

		return (subscriptionList != null && !subscriptionList.isEmpty()) ? subscriptionList.get(0) : null;
	}

}
