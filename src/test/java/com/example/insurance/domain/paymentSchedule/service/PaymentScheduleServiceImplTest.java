package com.example.insurance.domain.paymentSchedule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.example.insurance.domain.paymentSchedule.model.PaymentSchedule;
import com.example.insurance.domain.paymentSchedule.model.PaymentStatus;
import com.example.insurance.domain.paymentSchedule.repository.PaymentScheduleRepository;

class PaymentScheduleServiceImplTest {

    private PaymentScheduleRepository paymentScheduleRepository;
    private PaymentScheduleServiceImpl paymentScheduleService;

    @BeforeEach
    void setUp() {
        paymentScheduleRepository = mock(PaymentScheduleRepository.class);
        paymentScheduleService = new PaymentScheduleServiceImpl(paymentScheduleRepository);
    }

    @Test
    void refreshOverdueStatusesPersistsPastDuePendingSchedules() {
        PaymentSchedule overdueSchedule = new PaymentSchedule();
        overdueSchedule.setStatus(PaymentStatus.PENDING);
        overdueSchedule.setDueDate(LocalDate.now().minusDays(1));

        PaymentSchedule futureSchedule = new PaymentSchedule();
        futureSchedule.setStatus(PaymentStatus.PENDING);
        futureSchedule.setDueDate(LocalDate.now().plusDays(3));

        PaymentSchedule paidSchedule = new PaymentSchedule();
        paidSchedule.setStatus(PaymentStatus.PAID);
        paidSchedule.setDueDate(LocalDate.now().minusDays(10));

        List<PaymentSchedule> schedules = List.of(overdueSchedule, futureSchedule, paidSchedule);
        when(paymentScheduleRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<PaymentSchedule> refreshedSchedules = paymentScheduleService.refreshOverdueStatuses(schedules);

        assertSame(schedules, refreshedSchedules);
        assertEquals(PaymentStatus.OVERDUE, overdueSchedule.getStatus());
        assertEquals(PaymentStatus.PENDING, futureSchedule.getStatus());
        assertEquals(PaymentStatus.PAID, paidSchedule.getStatus());
        verify(paymentScheduleRepository).saveAll(List.of(overdueSchedule));
    }

    @Test
    void refreshOverdueStatusesSkipsRepositorySaveWhenNothingChanges() {
        PaymentSchedule futureSchedule = new PaymentSchedule();
        futureSchedule.setStatus(PaymentStatus.PENDING);
        futureSchedule.setDueDate(LocalDate.now().plusDays(1));

        paymentScheduleService.refreshOverdueStatuses(List.of(futureSchedule));

        assertEquals(PaymentStatus.PENDING, futureSchedule.getStatus());
        verify(paymentScheduleRepository, never()).saveAll(anyList());
    }
}
