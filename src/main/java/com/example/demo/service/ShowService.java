package com.example.demo.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CreateShowRequest;
import com.example.demo.entity.Seat;
import com.example.demo.entity.SeatStatus;
import com.example.demo.entity.Show;
import com.example.demo.repository.SeatRepository;
import com.example.demo.repository.ShowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShowService {

	private final ShowRepository showRepository;
	private final SeatRepository seatRepository;

	@Transactional
	public Show createShow(CreateShowRequest request) {

		Set<String> uniqueSeats = new LinkedHashSet<>(request.seats());

		if (uniqueSeats.size() != request.seats().size()) {
			throw new IllegalArgumentException("Duplicate seat numbers are not allowed");
		}

		Show show = Show.builder().name(request.name()).pricePaise(request.pricePaise())
				.perUserLimit(request.perUserLimit() == null ? 4 : request.perUserLimit()).build();

		showRepository.save(show);

		List<Seat> seats = uniqueSeats.stream()
				.map(number -> Seat.builder().show(show).seatNumber(number).status(SeatStatus.AVAILABLE).build())
				.toList();

		seatRepository.saveAll(seats);
		
		

		return show;
		
		
	}
}