
// package com.example.insurance.infrastructure.web.insurancePolicy;

// import java.util.List;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import
// com.example.insurance.domain.insurancePolicy.service.InsurancePolicyServices;
// import com.example.insurance.domain.insuranceProduct.model.InsuranceProduct;

// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/api/policy")
// public class InsurancePolicyController {

// private final InsurancePolicyServices insurancePolicyServices;

// @GetMapping("/all-policies")
// public List<InsuranceProduct> allPolicies() {
// return insurancePolicyServices.getAllPolicies();
// }

// @GetMapping("/policy-details/{policyId}")
// public InsuranceProduct getPolictyById(@PathVariable Long policyId) {
// return insurancePolicyServices.getPolicyById(policyId);
// }

// // @PostMapping("/buy-policy")
// // public ResponseEntity<?> buyPolicy(@RequestBody BuyPolicyDto buyPolicyDto)
// {

// // }
// }
