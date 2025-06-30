package com.example.insurance.domain.paymentSchedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.insurance.domain.paymentSchedule.model.PaymentSchedule;

@Repository
public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> {

}
