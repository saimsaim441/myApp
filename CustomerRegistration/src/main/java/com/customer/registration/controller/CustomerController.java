package com.customer.registration.controller;

import com.customer.registration.entity.Customer;
import com.customer.registration.service.CustomerService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class CustomerController {
	@Autowired
	private CustomerService customerService;

	@PostMapping("/register")
	public ResponseEntity<?> registerCustomer(@Valid @RequestBody Customer customer, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(bindingResult.getFieldErrors());
		}

		try {
			Customer savedCustomer = customerService.registerCustomer(customer);
			customerService.sendActivationEmail(savedCustomer);
			return ResponseEntity.ok("Customer registered successfully! Please check your email for activation.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/activate")
    public ResponseEntity<String> activateCustomer(@RequestParam String token) {
        try {
            String message = customerService.activateCustomer(token);
            return ResponseEntity.ok(message);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}
