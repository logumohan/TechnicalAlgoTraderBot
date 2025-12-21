package com.trading.platform.persistence;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.trading.platform.persistence.entity.Position;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
public class PositionInfoRepository implements PositionReadOnlyRepository {

	private static final String TRADE_ID_COLUMN = "tradeId";

	private static final String TARGET_ID_COLUMN = "targetId";

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Position findById(String tradeId, int targetId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Position> limitQuery = cb.createQuery(Position.class);
		Root<Position> from = limitQuery.from(Position.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(TRADE_ID_COLUMN), tradeId), cb.equal(from.get(TARGET_ID_COLUMN), targetId));
		limitQuery.orderBy(cb.asc(from.get(TARGET_ID_COLUMN)));

		List<Position> targetPositionList = entityManager.createQuery(limitQuery).getResultList();

		return targetPositionList.isEmpty() ? null : targetPositionList.get(0);
	}

	@Override
	public List<Position> findAllById(String tradeId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Position> limitQuery = cb.createQuery(Position.class);
		Root<Position> from = limitQuery.from(Position.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(TRADE_ID_COLUMN), tradeId));
		limitQuery.orderBy(cb.asc(from.get(TARGET_ID_COLUMN)));

		List<Position> targetPositionList = entityManager.createQuery(limitQuery).getResultList();

		return targetPositionList.isEmpty() ? Collections.emptyList() : targetPositionList;
	}

}
