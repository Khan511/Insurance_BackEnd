package com.example.insurance.domain.paymentSchedule.service;

import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.customerPolicy.model.PaymentFrequency;
import com.example.insurance.domain.paymentSchedule.model.PaymentSchedule;

import java.util.List;

public interface PaymentScheduleService {

    public List<PaymentSchedule> generatePaymentSchedule(CustomerPolicy policy, PaymentFrequency frequency);

    public List<PaymentSchedule> findByPolicyId(long id);

    public void processPayment(Long scheduleId);

    public List<PaymentSchedule> regeneratePaymentSchedule(CustomerPolicy policy, PaymentFrequency newFrequency);
}