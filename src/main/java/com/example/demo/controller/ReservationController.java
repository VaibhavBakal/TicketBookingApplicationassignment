package com.example.demo.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ReservationResponse;
import com.example.demo.dto.ReserveRequest;
import com.example.demo.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shows")
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationService reservationService;

	@PostMapping("/{showId}/reserve")
	public ResponseEntity<ReservationResponse> reserve(@PathVariable UUID showId,

			@Valid @RequestBody ReserveRequest request,

			Authentication authentication) {

		String userId = authentication.getName();

		ReservationResponse response = reservationService.reserve(showId, userId, request);

		return ResponseEntity.status(201).body(response);
	}
}
