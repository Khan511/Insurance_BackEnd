
package com.example.insurance.infrastructure.web.insuranceProduct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CategoryDto {

    private Long id;
    private String name;
    private String description;
}