package com.example.insurance.infrastructure.web.custommerPolicy;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.insurance.domain.customerPolicy.service.CustomerPolicyService;
import com.example.insurance.shared.kernel.utils.ResponseBuilder;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/policy")
public class CustomerPolicyController {

    private final CustomerPolicyService customerPolicyService;

    @PostMapping("/buy-policy")
    public ResponseEntity<?> buyPolicy(@RequestBody BuyPolicyDto buyPolicyDto, HttpServletRequest request) {
        customerPolicyService.saveCustomerPolicy(buyPolicyDto);

        return ResponseEntity.ok().body(
                ResponseBuilder.buildSuccess(request, null, "Policy has been bought successfully", HttpStatus.CREATED));
    }

}
