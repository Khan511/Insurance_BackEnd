package com.example.insurance.domain.paymentSchedule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.insurance.domain.paymentSchedule.model.PaymentSchedule;

@Repository
public interface PaymentScheduleRepository extends JpaRepository<PaymentSchedule, Long> {

    List<PaymentSchedule> findByPolicyId(long id);

    void deleteByPolicyId(Long id);

    // in PaymentScheduleRepository
    @Query("SELECT ps FROM PaymentSchedule ps " +
            "JOIN FETCH ps.policy p " +
            "JOIN FETCH p.policyHolder ph ")
    List<PaymentSchedule> findAllWithPolicyAndCustomer();

}
