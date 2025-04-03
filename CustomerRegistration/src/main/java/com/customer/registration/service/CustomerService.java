package com.customer.registration.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.customer.registration.entity.Customer;
import com.customer.registration.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
    private JavaMailSender javaMailSender;

	public Customer registerCustomer(Customer customer) {
		// Check if email is unique
		if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
			throw new IllegalArgumentException("Email already exists");
		}
		if (!customer.getPassword().equals(customer.getConfirmPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
		}
		// Generate activation token
		String token = UUID.randomUUID().toString();
		customer.setActivationToken(token);
		customer.setTokenExpiryDate(LocalDateTime.now().plusHours(24)); // Token valid for 24 hours

		// Save inactive customer to database
		customer.setActive(false);
		return customerRepository.save(customer);
	}

	
	public void sendActivationEmail(Customer customer) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(customer.getEmail());
		message.setSubject("Activate your account");
		String activationLink = "http://localhost:8080/api/activate?token=" + customer.getActivationToken();
		message.setText("Click the following link to activate your account: " + activationLink);

		javaMailSender.send(message);
	}

	
	@Transactional
	public String activateCustomer(String token) {
		Optional<Customer> customerOptional = customerRepository.findByActivationToken(token);

		if (customerOptional.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid activation token");
		}

		Customer customer = customerOptional.get();

		if (customer.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
			String newToken = UUID.randomUUID().toString();
			customer.setActivationToken(newToken);
			customer.setTokenExpiryDate(LocalDateTime.now().plusHours(24));
			customerRepository.save(customer);

			sendActivationEmail(customer);
			throw new ResponseStatusException(HttpStatus.GONE,
					"Activation token expired. A new activation link has been sent to your email.");
		}

		customer.setActive(true);
		customer.setActivationToken(null);
		customer.setTokenExpiryDate(null);
		customerRepository.save(customer);

		return "Account activated successfully.";
	}

}
