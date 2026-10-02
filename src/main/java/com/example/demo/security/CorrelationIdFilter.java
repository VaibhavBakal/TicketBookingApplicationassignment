package com.example.demo.security;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CorrelationIdFilter implements Filter {

	private static final String HEADER = "X-Request-Id";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;

		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String requestId = httpRequest.getHeader(HEADER);

		if (requestId == null || requestId.isBlank()) {

			requestId = UUID.randomUUID().toString();
		}

		MDC.put("requestId", requestId);

		httpResponse.setHeader(HEADER, requestId);

		try {

			chain.doFilter(request, response);

		} finally {

			MDC.remove("requestId");
		}
	}
}