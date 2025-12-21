package com.trading.platform.persistence;

import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import com.trading.platform.persistence.entity.Position;

@NoRepositoryBean
public interface PositionReadOnlyRepository extends Repository<Position, String> {

	Position findById(String tradeId, int targetId);

	List<Position> findAllById(String tradeId);

}
