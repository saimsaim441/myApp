package com.customer.registration.repository;

import java.util.Optional;

import com.customer.registration.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	Optional<Customer> findByEmail(String email);
	Optional<Customer> findByActivationToken(String activationToken);
}
