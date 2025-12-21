package com.trading.platform.service.trade;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.trading.platform.persistence.PositionInfoRepository;
import com.trading.platform.persistence.PositionRepository;
import com.trading.platform.persistence.entity.Position;
import com.trading.platform.util.TradeUtil;

@Component
public class PositionPersistence {

	private static final Logger LOGGER = LogManager.getLogger(PositionPersistence.class);

	@Autowired
	private PositionRepository positionRepository;

	@Autowired
	private PositionInfoRepository positionInfoRepository;

	public Position getById(String tradeId, int targetId) {
		return positionInfoRepository.findById(tradeId, targetId);
	}

	public List<Position> getByTradeId(String tradeId) {
		return positionInfoRepository.findAllById(tradeId);
	}

	public void persistTargetPosition(PositionInfo positionInfo) {
		LOGGER.info("persistTargetPosition: Converting target position info to trade position - {}",
				positionInfo);
		Position position = TradeUtil.convertToPosition(positionInfo);
		LOGGER.info("persistTargetPosition: Persisting the target position details in database - {}", position);
		positionRepository.save(position);
	}

	public void updateTargetPosition(PositionInfo positionInfo) {
		Position position = positionInfoRepository.findById(positionInfo.getTradeId(),
				positionInfo.getTargetId());
		if (position != null) {
			LOGGER.info("updateTargetPosition: Converting target position info to target position - {}",
					positionInfo);
			TradeUtil.updateTargetPositionStatus(position, positionInfo);
			LOGGER.info("updateTargetPosition: Updating the target position details in database - {}", position);
			positionRepository.save(position);
		} else {
			persistTargetPosition(positionInfo);
		}
	}

}
