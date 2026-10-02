package com.example.demo.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reservations", uniqueConstraints = {
		@UniqueConstraint(name = "uk_reservation_idempotency", columnNames = { "show_id", "user_id",
				"idempotency_key" }) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "show_id", nullable = false)
	private Show show;

	@Column(name = "user_id", nullable = false)
	private String userId;

	@Column(name = "idempotency_key", nullable = false)
	private String idempotencyKey;

	@Column(name = "amount_paise", nullable = false)
	private Long amountPaise;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReservationStatus status;

	@Column(nullable = false)
	private Instant createdAt;

	@Column
	private Instant cancelledAt;
}