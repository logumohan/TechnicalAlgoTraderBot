package com.trading.platform.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.trading.platform.persistence.entity.InstrumentIndicators;

@Repository
public interface InstrumentIndicatorsRepository extends JpaRepository<InstrumentIndicators, String> {

}
