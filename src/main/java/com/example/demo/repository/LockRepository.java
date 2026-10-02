package com.example.demo.repository;

import org.aspectj.apache.bcel.util.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LockRepository extends Repository {

	@Query(value = """
			    SELECT pg_advisory_xact_lock(
			        hashtextextended(:lockKey, 0)
			    )
			""", nativeQuery = true)
	void lock(@Param("lockKey") String lockKey);
}