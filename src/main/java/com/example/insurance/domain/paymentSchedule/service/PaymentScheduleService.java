package com.example.insurance.domain.paymentSchedule.service;

import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.paymentSchedule.model.PaymentSchedule;

import java.util.List;

public interface PaymentScheduleService {

    public List<PaymentSchedule> generatePaymentSchedule(CustomerPolicy policy, String frequency);

    public List<PaymentSchedule> findByPolicyId(long id);
}