package com.my.governance.repository;

import com.my.governance.entity.Election;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ElectionRepository extends ReactiveCrudRepository<Election, Long>, ReactiveSortingRepository<Election, Long> {
}
