package com.example.insurance.domain.customer.model;

import com.example.insurance.shared.kernel.embeddables.Address;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ContactInfo {
        @Column(name = "phone")
        private String phone;

        @Column(name = "alternatePhone")
        private String alternatePhone;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "street", column = @Column(name = "primary_street")),
                        @AttributeOverride(name = "city", column = @Column(name = "primary_city")),
                        // @AttributeOverride(name = "state", column = @Column(name = "primary_state")),
                        @AttributeOverride(name = "postalCode", column = @Column(name = "primary_postal_code")),
                        @AttributeOverride(name = "country", column = @Column(name = "primary_country"))
        })
        private Address primaryAddress;

        @Embedded
        @AttributeOverrides({
                        @AttributeOverride(name = "street", column = @Column(name = "billing_street")),
                        @AttributeOverride(name = "city", column = @Column(name = "billing_city")),
                        // @AttributeOverride(name = "state", column = @Column(name = "billing_state")),
                        @AttributeOverride(name = "postalCode", column = @Column(name = "billing_postal_code")),
                        @AttributeOverride(name = "country", column = @Column(name = "billing_country")),
        })
        private Address billingAddress;

}
