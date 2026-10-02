package com.example.demo.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Seat;
import com.example.demo.entity.SeatStatus;

import jakarta.persistence.LockModeType;

public interface SeatRepository extends JpaRepository<Seat, UUID> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			    SELECT s
			    FROM Seat s
			    WHERE s.show.id = :showId
			    AND s.seatNumber IN :seatNumbers
			    ORDER BY s.seatNumber
			""")
	List<Seat> findSeatsForUpdate(@Param("showId") UUID showId, @Param("seatNumbers") Collection<String> seatNumbers);

	List<Seat> findByShowIdOrderBySeatNumber(UUID showId);

	long countByShowIdAndStatus(UUID showId, SeatStatus status);
}