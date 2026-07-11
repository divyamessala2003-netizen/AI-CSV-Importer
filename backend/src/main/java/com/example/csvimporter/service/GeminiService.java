package com.example.csvimporter.service;

import com.example.csvimporter.ai.GeminiClient;
import com.example.csvimporter.ai.PromptBuilder;
import com.example.csvimporter.dto.CRMRecord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final GeminiClient geminiClient;
    private final PromptBuilder promptBuilder;

    public GeminiService(GeminiClient geminiClient, PromptBuilder promptBuilder) {
        this.geminiClient = geminiClient;
        this.promptBuilder = promptBuilder;
    }

    public List<CRMRecord> processCsvRecords(List<Map<String, String>> rawRecords) {
        List<CRMRecord> processedRecords = new ArrayList<>();
        int batchSize = 15; // Process 15 records at a time
        
        for (int i = 0; i < rawRecords.size(); i += batchSize) {
            int end = Math.min(i + batchSize, rawRecords.size());
            List<Map<String, String>> batch = rawRecords.subList(i, end);
            
            String systemInstructions = promptBuilder.buildSystemInstructions();
            String prompt = promptBuilder.buildPrompt(batch);
            
            List<CRMRecord> batchResult = geminiClient.processRecords(systemInstructions, prompt, batch);
            processedRecords.addAll(batchResult);
        }
        
        return processedRecords;
    }
}
