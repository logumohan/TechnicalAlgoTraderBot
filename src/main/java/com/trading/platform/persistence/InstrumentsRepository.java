package com.trading.platform.persistence;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.trading.platform.persistence.entity.Instrument;

@Repository
public interface InstrumentsRepository extends JpaRepository<Instrument, String> {

	@Modifying
	@Transactional
	@Query("delete from Instrument i where i.tickTime < :date")
	void deleteOlderThan(Date date);

}
