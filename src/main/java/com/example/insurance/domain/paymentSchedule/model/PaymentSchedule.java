package com.example.insurance.domain.paymentSchedule.model;

import java.time.LocalDate;
// import java.util.UUID;
import java.time.LocalDateTime;

import com.example.insurance.domain.customerPolicy.model.CustomerPolicy;
import com.example.insurance.shared.kernel.embeddables.MonetaryAmount;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_schedules")
public class PaymentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private CustomerPolicy policy;

    @Embedded
    private MonetaryAmount dueAmount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "paid_date")
    private LocalDateTime paidDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PaymentStatus status;

    @Column(name = "cancellation_date")
    private LocalDate cancellationDate;

    @Column(name = "cancelled_by")
    private String cancelledBy;

    // from payment gateway
    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Override
    public String toString() {
        return "Claim{" +
                "id=" + id +
                ", plicy='" + policy + '\'' +
                ", status=" + status +
                '}';
    }

    // Business method to cancel a payment schedule
    public void cancel(String cancelledByUser) {
        // Only set to CANCELLED if not already PAID
        if (this.status != PaymentStatus.PAID) {
            this.status = PaymentStatus.CANCELLED;
            this.cancellationDate = LocalDate.now();
            this.cancelledBy = cancelledByUser;
        }
    }

    // Business method to reactivate a cancelled payment schedule
    public void reactivate() {
        if (this.status == PaymentStatus.CANCELLED) {
            this.status = PaymentStatus.PENDING;
            this.cancellationDate = null;
            this.cancelledBy = null;
        }
    }

    // Check if payment can be cancelled
    public boolean canBeCancelled() {
        // Can cancel any schedule except PAID ones
        return this.status != PaymentStatus.PAID &&
                this.status != PaymentStatus.CANCELLED;
    }

    // Check if payment can be reactivated
    public boolean canBeReactivated() {
        return this.status == PaymentStatus.CANCELLED &&
                this.dueDate != null &&
                this.dueDate.isAfter(LocalDate.now());
    }

}
