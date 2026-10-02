package com.example.demo.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dto.ReservationResponse;
import com.example.demo.dto.ReserveRequest;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.ReservationSeat;
import com.example.demo.entity.ReservationStatus;
import com.example.demo.entity.Seat;
import com.example.demo.entity.SeatStatus;
import com.example.demo.entity.Show;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.ReservationSeatRepository;
import com.example.demo.repository.SeatRepository;
import com.example.demo.repository.ShowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final ShowRepository showRepository;
	private final SeatRepository seatRepository;
	private final ReservationRepository reservationRepository;
	private final ReservationSeatRepository reservationSeatRepository;

	@Transactional
	public ReservationResponse reserve(UUID showId, String userId, ReserveRequest request) {

		// ------------------------------------------------
		// 1. Lock show
		// ------------------------------------------------

		Show show = showRepository.findById(showId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Show not found"));

		// ------------------------------------------------
		// 2. Idempotency check
		// ------------------------------------------------

		Optional<Reservation> existing = reservationRepository.findByShowIdAndUserIdAndIdempotencyKey(showId, userId,
				request.idempotencyKey());

		if (existing.isPresent()) {

			Reservation reservation = existing.get();

			List<String> originalSeats = reservationSeatRepository.findAll().stream()
					.filter(rs -> rs.getReservation().getId().equals(reservation.getId()))
					.map(rs -> rs.getSeat().getSeatNumber()).sorted().toList();

			List<String> requestedSeats = request.seats().stream().distinct().sorted().toList();

			if (!originalSeats.equals(requestedSeats)) {

				throw new ResponseStatusException(HttpStatus.CONFLICT, "Idempotency key used with different seats");
			}

			return toResponse(reservation, originalSeats);
		}

		// ------------------------------------------------
		// 3. Validate duplicate seats
		// ------------------------------------------------

		List<String> requestedSeats = request.seats().stream().distinct().sorted().toList();

		if (requestedSeats.size() != request.seats().size()) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate seats requested");
		}

		// ------------------------------------------------
		// 4. Lock requested seats
		// ------------------------------------------------

		List<Seat> seats = seatRepository.findSeatsForUpdate(showId, requestedSeats);

		// ------------------------------------------------
		// 5. Verify every requested seat exists
		// ------------------------------------------------

		if (seats.size() != requestedSeats.size()) {

			throw new ResponseStatusException(HttpStatus.CONFLICT, "One or more seats do not exist");
		}

		// ------------------------------------------------
		// 6. Check seat availability
		// ------------------------------------------------

		boolean unavailable = seats.stream().anyMatch(seat -> seat.getStatus() != SeatStatus.AVAILABLE);

		if (unavailable) {

			throw new ResponseStatusException(HttpStatus.CONFLICT, "One or more seats are already taken");
		}

		// ------------------------------------------------
		// 7. Check user limit
		// ------------------------------------------------

		long currentSeats = reservationSeatRepository.countConfirmedSeats(showId, userId);

		if (currentSeats + seats.size() > show.getPerUserLimit()) {

			throw new ResponseStatusException(HttpStatus.CONFLICT, "Per-user seat limit exceeded");
		}

		// ------------------------------------------------
		// 8. Calculate money
		// ------------------------------------------------

		long amount = show.getPricePaise() * seats.size();

		// ------------------------------------------------
		// 9. Create reservation
		// ------------------------------------------------

		Reservation reservation = Reservation.builder().show(show).userId(userId)
				.idempotencyKey(request.idempotencyKey()).amountPaise(amount).status(ReservationStatus.CONFIRMED)
				.createdAt(Instant.now()).build();

		reservationRepository.save(reservation);

		// ------------------------------------------------
		// 10. Change seats
		// ------------------------------------------------

		List<ReservationSeat> reservationSeats = new ArrayList<>();

		for (Seat seat : seats) {

			seat.setStatus(SeatStatus.CONFIRMED);

			reservationSeats.add(ReservationSeat.builder().reservation(reservation).seat(seat).build());
		}

		reservationSeatRepository.saveAll(reservationSeats);

		// ------------------------------------------------
		// 11. Return
		// ------------------------------------------------

		return new ReservationResponse(reservation.getId(), show.getId(), userId, requestedSeats, amount, "confirmed");
	}

	private ReservationResponse toResponse(Reservation reservation, List<String> seats) {

		return new ReservationResponse(reservation.getId(), reservation.getShow().getId(), reservation.getUserId(),
				seats, reservation.getAmountPaise(), reservation.getStatus().name().toLowerCase());
	}
}