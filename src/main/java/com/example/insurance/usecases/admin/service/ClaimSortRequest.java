package com.example.insurance.usecases.admin.service;

import lombok.Data;

@Data
public class ClaimSortRequest {

    private String sortBy = "submissionDate";
    private String sortDirection = "ASC";

}
