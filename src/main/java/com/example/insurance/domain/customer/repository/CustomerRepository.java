package com.example.insurance.domain.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.insurance.domain.customer.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
