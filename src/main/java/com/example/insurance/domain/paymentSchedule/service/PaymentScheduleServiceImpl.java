package com.example.insurance.domain.paymentSchedule.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.customerPolicy.model.PaymentFrequency;
import com.example.insurance.domain.paymentSchedule.model.PaymentSchedule;
import com.example.insurance.domain.paymentSchedule.repository.PaymentScheduleRepository;

import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentScheduleServiceImpl implements PaymentScheduleService {

    private final PaymentScheduleRepository paymentScheduleRepository;

    public List<PaymentSchedule> generatePaymentSchedule(CustomerPolicy policy, PaymentFrequency frequency) {
        List<PaymentSchedule> schedules = new ArrayList<>();
        MonetaryAmount premium = policy.getPremium();
        LocalDate startDate = policy.getCoveragePeriod().getEffectiveDate();

        int numberOfPayments;
        int periodMonths;

        switch (frequency) {
            case PaymentFrequency.MONTHLY:
                numberOfPayments = 12;
                periodMonths = 1;
                break;
            case PaymentFrequency.QUARTERLY:
                numberOfPayments = 4;
                periodMonths = 3;
                break;
            case PaymentFrequency.ANNUAL:
                numberOfPayments = 1;
                periodMonths = 12;
                break;
            default:
                throw new IllegalArgumentException("Unsupported payment frequency: " + frequency);
        }

        BigDecimal installmentAmount = premium.getAmount()
                .divide(BigDecimal.valueOf(numberOfPayments), 2, RoundingMode.HALF_UP);

        MonetaryAmount installmentMonetaryAmount = new MonetaryAmount(
                installmentAmount,
                premium.getCurrency());

        for (int i = 0; i < numberOfPayments; i++) {
            PaymentSchedule schedule = new PaymentSchedule();
            schedule.setPolicy(policy);
            schedule.setDueAmount(installmentMonetaryAmount);
            schedule.setDueDate(startDate.plusMonths(i * periodMonths));
            schedules.add(schedule);
        }

        return schedules;
    }

    // Regenrate payment shcedules(To clear old ones)
    @Transactional
    public List<PaymentSchedule> regeneratePaymentSchedule(CustomerPolicy policy, PaymentFrequency newFrequency) {

        // Clear existing one
        paymentScheduleRepository.deleteByPolicyId(policy.getId());

        // Generate new schedule
        return generatePaymentSchedule(policy, newFrequency);
    }

    @Override
    public List<PaymentSchedule> findByPolicyId(long id) {
        return paymentScheduleRepository.findByPolicyId(id);
    }

}
