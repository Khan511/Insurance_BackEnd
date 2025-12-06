package com.example.insurance.domain.customer.repository;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.example.insurance.domain.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUserId(String userId);

}
