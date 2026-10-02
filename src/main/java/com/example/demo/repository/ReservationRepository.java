package com.example.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

	Optional<Reservation> findByShowIdAndUserIdAndIdempotencyKey(UUID showId, String userId, String idempotencyKey);

	long countByShowIdAndUserIdAndStatus(UUID showId, String userId, ReservationStatus status);
}