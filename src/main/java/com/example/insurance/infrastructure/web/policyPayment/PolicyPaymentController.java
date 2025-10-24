package com.example.insurance.infrastructure.web.policyPayment;

import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.domain.customerPolicy.service.CustomerPolicyService;
import com.example.insurance.domain.paymentSchedule.model.PaymentSchedule;
import com.example.insurance.domain.paymentSchedule.service.PaymentScheduleService;
import com.example.insurance.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my-policies")
public class PolicyPaymentController {

    private final CustomerPolicyService customerPolicyService;

    private final PaymentScheduleService paymentScheduleService;

    @GetMapping("/payments")
    public ResponseEntity<?> getMyPolicyPayments(@AuthenticationPrincipal User user) {
        try {
            List<CustomerPolicy> policies = customerPolicyService.findByUserId(user.getUserId());
            // List<CustomerPolicy> policies =
            // customerPolicyService.findByUserId(user.getId());

            List<Map<String, Object>> result = policies.stream().map(policy -> {
                Map<String, Object> policyData = new HashMap<>();
                policyData.put("id", policy.getId());
                policyData.put("policyNumber", policy.getPolicyNumber());
                policyData.put("productType", policy.getProduct().getProductType().toString());
                policyData.put("premium", Map.of(
                        "amount", policy.getPremium().getAmount(),
                        "currency", policy.getPremium().getCurrency()));

                // Get payment schedule
                List<PaymentSchedule> payments = paymentScheduleService.findByPolicyId(policy.getId());
                List<Map<String, Object>> paymentData = payments.stream().map(payment -> {
                    Map<String, Object> paymentMap = new HashMap<>();
                    paymentMap.put("id", payment.getId());
                    paymentMap.put("dueAmount", Map.of(
                            "amount", payment.getDueAmount().getAmount(),
                            "currency", payment.getDueAmount().getCurrency()));
                    paymentMap.put("dueDate", payment.getDueDate().toString());
                    paymentMap.put("paidDate", payment.getPaidDate() != null ? payment.getPaidDate().toString() : null);

                    // // Determine payment status
                    // String status = "pending";
                    // if (payment.getPaidDate() != null) {
                    // status = "paid";
                    // } else if (payment.getDueDate().isBefore(java.time.LocalDate.now())) {
                    // status = "overdue";
                    // }
                    paymentMap.put("status", payment.getStatus());

                    return paymentMap;
                }).collect(Collectors.toList());

                policyData.put("paymentSchedule", paymentData);
                return policyData;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Map.of("error", "Failed to retrieve payment information: " + e.getMessage()));
        }
    }

    @PostMapping("/process-payment/{scheduleId}")
    public ResponseEntity<?> processPayment(@PathVariable long scheduleId) {

        paymentScheduleService.processPayment(scheduleId);
        return ResponseEntity.ok().body(Map.of("message", "Payment Payid Successfully"));
    }

}