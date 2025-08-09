package com.example.insurance.infrastructure.web.claim;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class DocumentTypeDTO {
    public DocumentTypeDTO(String name2, String displayName2) {
        // TODO Auto-generated constructor stub
    }

    private String name;
    private String displayName;

}
