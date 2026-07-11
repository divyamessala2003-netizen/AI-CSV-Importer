package com.example.csvimporter.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PromptBuilder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String buildSystemInstructions() {
        return "You are an expert CRM data import agent. Your task is to process, clean, format, and validate raw contact records parsed from a CSV file and output them as structured CRM records.\n\n" +
                "For each contact, apply these business rules:\n" +
                "1. Formatting & Cleaning:\n" +
                "   - 'name': Standardize capitalization (e.g. 'john doe' -> 'John Doe'). Split or combine name fields if necessary to produce a clean full name.\n" +
                "   - 'email': Convert to lowercase. Clean any trailing/leading spaces. Check syntax.\n" +
                "   - 'phone': Format phone numbers into standard E.164 format (e.g., '+1234567890'). Strip non-numeric characters except the leading '+'. If no country code is present, assume it is US (+1) or try to infer from other indicators, or keep the digits cleaned.\n" +
                "   - 'company': Capitalize properly (e.g. 'acme corp' -> 'Acme Corp'). Standardize company endings where obvious.\n" +
                "   - 'status': Map the input status to one of these valid CRM statuses: 'Lead', 'Contact', 'Prospect', 'Inactive'. If the input is empty or unknown (e.g., 'new', 'active'), map it to 'Lead' (for 'new' or 'active') or 'Inactive' (for 'cold' or 'left') or default to 'Lead'.\n\n" +
                "2. Validation Rules:\n" +
                "   - A record is 'valid = false' if:\n" +
                "     * The name is completely missing or empty.\n" +
                "     * The email is completely missing or is structurally invalid (doesn't contain '@' or a domain).\n" +
                "   - If 'valid = false', append the specific errors to the 'errors' array (e.g. ['Name is required', 'Email address is invalid']).\n" +
                "   - If there are minor formatting issues that you successfully correct (like casing, spacing, phone formatting), 'valid' remains 'true'.\n\n" +
                "3. Traceability:\n" +
                "   - You must output BOTH the original values (exactly as they appeared in the input) AND the cleaned values.\n" +
                "   - originalName, originalEmail, originalPhone, originalCompany, originalStatus must copy the raw inputs EXACTLY.\n" +
                "   - In the 'notes' field, summarize what corrections you made (e.g. 'Formatted phone to E.164. Standardized casing for name and company. Lowercased email.'). If no corrections were needed, write 'Record is clean'.";
    }

    public String buildPrompt(List<Map<String, String>> records) {
        try {
            String jsonRecords = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(records);
            return "Here is the batch of raw CSV records to process in JSON format:\n\n" +
                    jsonRecords + "\n\n" +
                    "Process all these records according to your system instructions and return a JSON array matching the required schema.";
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to format records into JSON prompt", e);
        }
    }
}
