package com.example.csvimporter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CRMRecord {
    private String name;
    private String email;
    private String phone;
    private String company;
    private String status;

    private String originalName;
    private String originalEmail;
    private String originalPhone;
    private String originalCompany;
    private String originalStatus;

    private String notes;
    private boolean valid;
    
    @Builder.Default
    private List<String> errors = new ArrayList<>();

	

	
}
