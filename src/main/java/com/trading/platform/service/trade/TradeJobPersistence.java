package com.trading.platform.service.trade;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trading.platform.persistence.TradeInfoRepository;
import com.trading.platform.persistence.TradeRepository;
import com.trading.platform.persistence.entity.InstrumentSubscription;
import com.trading.platform.persistence.entity.Job;
import com.trading.platform.persistence.entity.Position;
import com.trading.platform.persistence.entity.Signal;
import com.trading.platform.persistence.entity.Trade;
import com.trading.platform.persistence.entity.UserAccount;
import com.trading.platform.util.TradeUtil;

@Component
public class TradeJobPersistence {

	private static final Logger LOGGER = LogManager.getLogger(TradeJobPersistence.class);

	@Autowired
	private PositionPersistence positionPersistence;

	@Autowired
	private TradeRepository tradeRepository;

	@Autowired
	private TradeInfoRepository tradeInfoRepository;

	public boolean isActiveLiveTradeExists(Signal signal, Job job) {
		List<Trade> tradeList = tradeInfoRepository.findActiveLiveTrades();

		Predicate<Trade> tradeFilter = (Trade trade) -> (trade.getAggregationType().equals(signal.getAggregationType())
				&& trade.getToken() == signal.getToken()
				&& trade.getJobName().equals(job.getName())
				&& trade.getUserName().equals(job.getUserName())
				&& trade.getTradeSignal().equals(signal.getTradeSignal()));

		long count = tradeList.stream().filter(tradeFilter).count();

		return count > 0;
	}

	public boolean isActivePaperTradeExists(Signal signal, Job job) {
		List<Trade> tradeList = tradeInfoRepository.findActivePaperTrades();

		Predicate<Trade> tradeFilter = (Trade trade) -> (trade.getAggregationType().equals(signal.getAggregationType())
				&& trade.getToken() == signal.getToken()
				&& trade.getJobName().equals(job.getName())
				&& trade.getUserName().equals(job.getUserName())
				&& trade.getTradeSignal().equals(signal.getTradeSignal()));

		long count = tradeList.stream().filter(tradeFilter).count();

		return count > 0;
	}

	public boolean isConsecutiveTradesFailed(Signal signal, InstrumentSubscription subscription) {
		List<Trade> tradeList = tradeInfoRepository.findClosedLiveTradesLimitedTo(signal.getToken(),
				subscription.getConsecutiveFailedTradesPerDay());
		long failedTrades = tradeList.stream().filter(trade -> getProfitByTrade(trade) < 0).count();

		return failedTrades == subscription.getConsecutiveFailedTradesPerDay();
	}

	public double getProfitByUser(UserAccount user) {
		List<Trade> tradeList = tradeInfoRepository.findAllLiveTrades();
		return tradeList.stream()
				.filter((Trade trade) -> trade.getUserName().equals(user.getUserName()))
				.filter((Trade trade) -> !isTradeActive(trade))
				.map(this::getProfitByTrade)
				.collect(Collectors.summingDouble(Double::doubleValue));
	}

	public int getAllLiveTradeCount() {
		List<Trade> tradeList = tradeInfoRepository.findAllLiveTrades();
		return tradeList.size();
	}

	public int getAllLiveTradeCount(long token) {
		List<Trade> tradeList = tradeInfoRepository.findAllLiveTrades(token);
		return tradeList.size();
	}

	public int getActiveLiveTradeCount() {
		List<Trade> tradeList = tradeInfoRepository.findActiveLiveTrades();
		return tradeList.size();
	}

	public int getActiveLiveTradeCount(long token) {
		List<Trade> tradeList = tradeInfoRepository.findActiveLiveTrades(token);
		return tradeList.size();
	}

	public int getAllPaperTradeCount() {
		List<Trade> tradeList = tradeInfoRepository.findAllPaperTrades();
		return tradeList.size();
	}

	public int getAllPaperTradeCount(long token) {
		List<Trade> tradeList = tradeInfoRepository.findAllPaperTrades(token);
		return tradeList.size();
	}

	public int getActivePaperTradeCount() {
		List<Trade> tradeList = tradeInfoRepository.findActivePaperTrades();
		return tradeList.size();
	}

	public int getActivePaperTradeCount(long token) {
		List<Trade> tradeList = tradeInfoRepository.findActivePaperTrades(token);
		return tradeList.size();
	}

	public Trade getByTradeId(String tradeId) {
		return tradeInfoRepository.findById(tradeId);
	}

	public List<Trade> getActivePaperTrades() {
		return tradeInfoRepository.findActivePaperTrades();
	}

	public List<Trade> getActiveLiveTrades() {
		return tradeInfoRepository.findActiveLiveTrades();
	}

	public boolean isTradeActive(Trade trade) {
		List<Position> positionList = positionPersistence.getByTradeId(trade.getTradeId());
		return positionList.stream().filter(Position::isActive).count() > 0;
	}

	public double getProfitByTrade(Trade trade) {
		List<Position> positionList = positionPersistence.getByTradeId(trade.getTradeId());
		return positionList.stream().filter(Position::isClosed).collect(Collectors.summingDouble(Position::getProfit));
	}

	public void persistTrade(TradeInfo tradeInfo) {
		LOGGER.info("persistTrade: Converting trade info to trade - {}", tradeInfo);
		Trade trade = TradeUtil.convertToTrade(tradeInfo);
		LOGGER.info("persistTrade: Persisting the trade details in database - {}", trade);
		tradeRepository.save(trade);
		for (PositionInfo positionInfo : tradeInfo.getPositionInfoList()) {
			positionPersistence.persistTargetPosition(positionInfo);
		}
	}

	public void updateTrade(TradeInfo tradeInfo) {
		Trade trade = tradeInfoRepository.findById(tradeInfo.getTradeId());
		if (trade != null) {
			LOGGER.info("updateTrade: Converting trade info to trade - {}", tradeInfo);
			TradeUtil.updateTradeStatus(trade, tradeInfo);
			LOGGER.info("updateTrade: Updating the trade details in database - {}", trade);
			tradeRepository.save(trade);
			for (PositionInfo positionInfo : tradeInfo.getPositionInfoList()) {
				positionPersistence.updateTargetPosition(positionInfo);
			}
		} else {
			persistTrade(tradeInfo);
		}
	}

}
