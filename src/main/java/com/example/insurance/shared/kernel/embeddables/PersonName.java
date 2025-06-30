package com.example.insurance.shared.kernel.embeddables;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class PersonName {
    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;
    // private String middleName;

}
