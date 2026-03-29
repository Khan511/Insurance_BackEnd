package com.example.insurance.domain.customerPolicy.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.customerPolicy.repository.CustomerPolicyRepository;
import com.example.insurance.shared.enummuration.PolicyStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PolicyStatusUpdateService {

    private final CustomerPolicyRepository customerPolicyRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updatePolicyStatus() {

        LocalDate today = LocalDate.now();

        // Update INACTIVE policies whose effective date has arrived
        List<CustomerPolicy> inactivePolicies = customerPolicyRepository.findByStatus(PolicyStatus.INACTIVE);

        inactivePolicies.forEach(policy -> {
            LocalDate efectiveDate = policy.getCoveragePeriod().getEffectiveDate();

            if (!efectiveDate.isAfter(today)) {
                policy.setStatus(PolicyStatus.ACTIVE);
            }
        });

        customerPolicyRepository.saveAll(inactivePolicies);

        // Update ACTIVE polices that have expired
        List<CustomerPolicy> activePolicies = customerPolicyRepository.findByStatus(PolicyStatus.ACTIVE);

        activePolicies.forEach(policy -> {
            LocalDate expireDate = policy.getCoveragePeriod().getExpirationDate();

            if (expireDate.isBefore(today)) {
                policy.setStatus(PolicyStatus.EXPIRED);
            }
        });

        customerPolicyRepository.saveAll(activePolicies);
    }

}
