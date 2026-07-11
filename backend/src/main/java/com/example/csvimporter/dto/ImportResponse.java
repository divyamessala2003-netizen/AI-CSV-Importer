package com.example.csvimporter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResponse {
    private int totalRecords;
    private int successfulRecords;
    private int correctedRecords;
    private int failedRecords;
    private List<CRMRecord> records;
}
