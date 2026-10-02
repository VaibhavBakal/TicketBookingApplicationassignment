package com.example.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.ReservationSeat;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, UUID> {

	@Query("""
			    SELECT COUNT(rs)
			    FROM ReservationSeat rs
			    WHERE rs.reservation.show.id = :showId
			    AND rs.reservation.userId = :userId
			    AND rs.reservation.status = 'CONFIRMED'
			""")
	long countConfirmedSeats(@Param("showId") UUID showId, @Param("userId") String userId);
}