package com.trading.platform.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trading.platform.persistence.entity.Trade;

@Repository
public interface TradeRepository extends JpaRepository<Trade, String> {

}
