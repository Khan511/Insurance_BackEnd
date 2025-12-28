package com.example.insurance.usecases.admin.service;

import java.util.List;

import com.example.insurance.usecases.admin.controller.AdminAllPaymentsDto;

public record PaymentSummaryDto(

        List<AdminAllPaymentsDto> coming,
        List<AdminAllPaymentsDto> overdue,
        List<AdminAllPaymentsDto> paid,
        List<AdminAllPaymentsDto> paused,
        List<AdminAllPaymentsDto> cancelled) {

}
