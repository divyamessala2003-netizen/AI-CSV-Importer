package com.example.csvimporter.service;

import com.example.csvimporter.dto.CRMRecord;
import com.example.csvimporter.dto.ImportResponse;
import com.example.csvimporter.dto.PreviewResponse;
import com.example.csvimporter.util.CsvParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsvService {

    private final CsvParser csvParser;
    private final GeminiService geminiService;

    public CsvService(CsvParser csvParser, GeminiService geminiService) {
        this.csvParser = csvParser;
        this.geminiService = geminiService;
    }

    public PreviewResponse getPreview(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            List<List<String>> raw = csvParser.parseCsvAsLists(is);
            if (raw.isEmpty()) {
                return PreviewResponse.builder()
                        .headers(new ArrayList<>())
                        .rows(new ArrayList<>())
                        .build();
            }
            
            List<String> headers = raw.get(0);
            List<Map<String, String>> rows = new ArrayList<>();
            
            // Limit preview to first 5 rows
            int previewLimit = Math.min(raw.size(), 6); 
            for (int i = 1; i < previewLimit; i++) {
                List<String> row = raw.get(i);
                Map<String, String> map = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    String header = headers.get(j);
                    String val = j < row.size() ? row.get(j) : "";
                    map.put(header, val);
                }
                rows.add(map);
            }
            
            return PreviewResponse.builder()
                    .headers(headers)
                    .rows(rows)
                    .build();
        }
    }

    public ImportResponse importCsv(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            List<Map<String, String>> rawRecords = csvParser.parseCsvAsMaps(is);
            if (rawRecords.isEmpty()) {
                return ImportResponse.builder()
                        .records(new ArrayList<>())
                        .build();
            }
            
            List<CRMRecord> processed = geminiService.processCsvRecords(rawRecords);
            
            int total = processed.size();
            int successful = 0;
            int corrected = 0;
            int failed = 0;
            
            for (CRMRecord r : processed) {
                if (!r.isValid()) {
                    failed++;
                } else {
                    successful++;
                    // Check if AI cleaned/corrected any value from the original
                    boolean isCorrected = false;
                    
                    String normName = r.getName() != null ? r.getName().trim() : "";
                    String normOrigName = r.getOriginalName() != null ? r.getOriginalName().trim() : "";
                    if (!normName.equalsIgnoreCase(normOrigName)) isCorrected = true;
                    
                    String normEmail = r.getEmail() != null ? r.getEmail().trim() : "";
                    String normOrigEmail = r.getOriginalEmail() != null ? r.getOriginalEmail().trim() : "";
                    if (!normEmail.equalsIgnoreCase(normOrigEmail)) isCorrected = true;
                    
                    String normPhone = r.getPhone() != null ? r.getPhone().replaceAll("[^0-9]", "") : "";
                    String normOrigPhone = r.getOriginalPhone() != null ? r.getOriginalPhone().replaceAll("[^0-9]", "") : "";
                    if (!normPhone.equals(normOrigPhone)) isCorrected = true;
                    
                    String normCompany = r.getCompany() != null ? r.getCompany().trim() : "";
                    String normOrigCompany = r.getOriginalCompany() != null ? r.getOriginalCompany().trim() : "";
                    if (!normCompany.equalsIgnoreCase(normOrigCompany)) isCorrected = true;
                    
                    String normStatus = r.getStatus() != null ? r.getStatus().trim() : "";
                    String normOrigStatus = r.getOriginalStatus() != null ? r.getOriginalStatus().trim() : "";
                    if (!normStatus.equalsIgnoreCase(normOrigStatus)) isCorrected = true;
                    
                    if (isCorrected) {
                        corrected++;
                    }
                }
            }
            
            return ImportResponse.builder()
                    .totalRecords(total)
                    .successfulRecords(successful)
                    .correctedRecords(corrected)
                    .failedRecords(failed)
                    .records(processed)
                    .build();
        }
    }
}
