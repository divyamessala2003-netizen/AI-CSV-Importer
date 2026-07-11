package com.example.csvimporter.controller;

import com.example.csvimporter.dto.ImportResponse;
import com.example.csvimporter.dto.PreviewResponse;
import com.example.csvimporter.service.CsvService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/csv")
public class CsvController {

    private final CsvService csvService;

    public CsvController(CsvService csvService) {
        this.csvService = csvService;
    }

    @PostMapping("/preview")
    public ResponseEntity<?> previewCsv(@RequestParam("file") MultipartFile file) throws Exception {

        PreviewResponse response = csvService.getPreview(file);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importCsv(@RequestParam("file") MultipartFile file) throws Exception {

        ImportResponse response = csvService.importCsv(file);

        return ResponseEntity.ok(response);
    }

}