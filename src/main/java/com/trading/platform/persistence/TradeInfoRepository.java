package com.trading.platform.persistence;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.trading.platform.persistence.entity.Trade;
import com.trading.platform.util.MarketTimeUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository
public class TradeInfoRepository implements TradeReadOnlyRepository {

	private static final String TICK_TIME_COLUMN = "tickTime";

	private static final String TOKEN_COLUMN = "token";

	private static final String IS_LIVE_COLUMN = "isLive";

	private static final String IS_ACTIVE_COLUMN = "isActive";

	private static final String TRADE_ID_COLUMN = "tradeId";

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Trade findById(String tradeId) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(TRADE_ID_COLUMN), tradeId));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		List<Trade> tradeList = entityManager.createQuery(limitQuery).getResultList();

		return tradeList.isEmpty() ? null : tradeList.get(0);
	}

	@Override
	public List<Trade> findAllTrades() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
				Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Trade> findAllLiveTrades() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(IS_LIVE_COLUMN), true),
				cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
						Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Trade> findAllLiveTrades(long token) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(
				cb.equal(from.get(TOKEN_COLUMN), token),
				cb.equal(from.get(IS_LIVE_COLUMN), true),
				cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
						Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Trade> findAllPaperTrades() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(IS_LIVE_COLUMN), false),
				cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
						Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Trade> findAllPaperTrades(long token) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(
				cb.equal(from.get(TOKEN_COLUMN), token),
				cb.equal(from.get(IS_LIVE_COLUMN), false),
				cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
						Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Trade> findActiveLiveTrades() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(IS_ACTIVE_COLUMN), true),
				cb.equal(from.get(IS_LIVE_COLUMN), true),
				cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
						Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Trade> findActiveLiveTrades(long token) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(
				cb.equal(from.get(TOKEN_COLUMN), token),
				cb.equal(from.get(IS_ACTIVE_COLUMN), true),
				cb.equal(from.get(IS_LIVE_COLUMN), true),
				cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
						Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Trade> findActivePaperTrades() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(IS_ACTIVE_COLUMN), true),
				cb.equal(from.get(IS_LIVE_COLUMN), false),
				cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
						Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Trade> findActivePaperTrades(long token) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(
				cb.equal(from.get(TOKEN_COLUMN), token),
				cb.equal(from.get(IS_ACTIVE_COLUMN), true),
				cb.equal(from.get(IS_LIVE_COLUMN), false),
				cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
						Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Trade> findClosedLiveTrades() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(IS_ACTIVE_COLUMN), false),
				cb.equal(from.get(IS_LIVE_COLUMN), true),
				cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
						Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Trade> findClosedLiveTrades(long token) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(
				cb.equal(from.get(TOKEN_COLUMN), token),
				cb.equal(from.get(IS_ACTIVE_COLUMN), false),
				cb.equal(from.get(IS_LIVE_COLUMN), true),
				cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
						Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Trade> findClosedLiveTradesLimitedTo(long token, int maxResults) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(
				cb.equal(from.get(TOKEN_COLUMN), token),
				cb.equal(from.get(IS_ACTIVE_COLUMN), false),
				cb.equal(from.get(IS_LIVE_COLUMN), true),
				cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
						Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).setMaxResults(maxResults).getResultList();
	}

	@Override
	public List<Trade> findClosedPaperTrades() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(IS_ACTIVE_COLUMN), false),
				cb.equal(from.get(IS_LIVE_COLUMN), false),
				cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
						Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Trade> findClosedPaperTrades(long token) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(
				cb.equal(from.get(TOKEN_COLUMN), token),
				cb.equal(from.get(IS_ACTIVE_COLUMN), false),
				cb.equal(from.get(IS_LIVE_COLUMN), false),
				cb.greaterThanOrEqualTo(from.get(TICK_TIME_COLUMN),
						Date.from(MarketTimeUtil.getMarketStartTime().toInstant())));
		limitQuery.orderBy(cb.desc(from.get(TICK_TIME_COLUMN)));

		return entityManager.createQuery(limitQuery).getResultList();
	}

	@Override
	public List<Trade> findHistoricalTrades(long token, boolean isLive) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Trade> limitQuery = cb.createQuery(Trade.class);
		Root<Trade> from = limitQuery.from(Trade.class);
		limitQuery.select(from);
		limitQuery.where(cb.equal(from.get(TOKEN_COLUMN), token),
				cb.equal(from.get(IS_LIVE_COLUMN), isLive));

		return entityManager.createQuery(limitQuery).getResultList();
	}

}
