package com.trading.platform.persistence;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import com.trading.platform.persistence.entity.Trade;

@NoRepositoryBean
public interface TradeReadOnlyRepository extends Repository<Trade, String> {

	Trade findById(String tradeId);

	List<Trade> findAllTrades();

	List<Trade> findAllLiveTrades();

	List<Trade> findAllLiveTrades(long token);

	List<Trade> findAllPaperTrades();

	List<Trade> findAllPaperTrades(long token);

	List<Trade> findActiveLiveTrades();

	List<Trade> findActiveLiveTrades(long token);

	List<Trade> findActivePaperTrades();

	List<Trade> findActivePaperTrades(long token);

	List<Trade> findClosedLiveTrades();

	List<Trade> findClosedLiveTrades(long token);

	List<Trade> findClosedLiveTradesLimitedTo(long token, int maxResults);

	List<Trade> findClosedPaperTrades();

	List<Trade> findClosedPaperTrades(long token);

	List<Trade> findHistoricalTrades(long token, boolean isLive);

}
