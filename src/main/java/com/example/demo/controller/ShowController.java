package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CreateShowRequest;
import com.example.demo.entity.Show;
import com.example.demo.service.ShowService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/shows")
@RequiredArgsConstructor
public class ShowController {

	private final ShowService showService;

	@PostMapping
	public ResponseEntity<Show> createShow(@Valid @RequestBody CreateShowRequest request) {

		return ResponseEntity.status(201).body(showService.createShow(request));
	}
}