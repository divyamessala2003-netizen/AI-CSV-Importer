package com.example.csvimporter.ai;

import com.example.csvimporter.dto.CRMRecord;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key:}")
    private String configuredApiKey;

    public List<CRMRecord> processRecords(String systemInstructions, String prompt, List<Map<String, String>> rawBatch) {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.warn("GEMINI_API_KEY is not set. Falling back to Mock AI Processing.");
            return generateMockResponse(rawBatch);
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        try {
            // Build the JSON payload request manually or using Maps to avoid complex DTOs for the request
            Map<String, Object> requestBody = new LinkedHashMap<>();

            // contents
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);
            Map<String, Object> content = new HashMap<>();
            content.put("parts", Collections.singletonList(part));
            requestBody.put("contents", Collections.singletonList(content));

            // systemInstruction
            Map<String, Object> sysPart = new HashMap<>();
            sysPart.put("text", systemInstructions);
            Map<String, Object> sysInstruction = new HashMap<>();
            sysInstruction.put("parts", Collections.singletonList(sysPart));
            requestBody.put("systemInstruction", sysInstruction);

            // generationConfig
            Map<String, Object> generationConfig = new LinkedHashMap<>();
            generationConfig.put("responseMimeType", "application/json");

            // responseSchema
            Map<String, Object> schema = new LinkedHashMap<>();
            schema.put("type", "ARRAY");

            Map<String, Object> items = new LinkedHashMap<>();
            items.put("type", "OBJECT");

            Map<String, Object> properties = new LinkedHashMap<>();
            properties.put("name", Map.of("type", "STRING"));
            properties.put("email", Map.of("type", "STRING"));
            properties.put("phone", Map.of("type", "STRING"));
            properties.put("company", Map.of("type", "STRING"));
            properties.put("status", Map.of("type", "STRING"));
            
            properties.put("originalName", Map.of("type", "STRING"));
            properties.put("originalEmail", Map.of("type", "STRING"));
            properties.put("originalPhone", Map.of("type", "STRING"));
            properties.put("originalCompany", Map.of("type", "STRING"));
            properties.put("originalStatus", Map.of("type", "STRING"));

            properties.put("notes", Map.of("type", "STRING"));
            properties.put("valid", Map.of("type", "BOOLEAN"));
            
            Map<String, Object> errorsProperty = new LinkedHashMap<>();
            errorsProperty.put("type", "ARRAY");
            errorsProperty.put("items", Map.of("type", "STRING"));
            properties.put("errors", errorsProperty);

            items.put("properties", properties);
            items.put("required", List.of("name", "email", "phone", "company", "status", "originalName", "originalEmail", "originalPhone", "originalCompany", "originalStatus", "valid"));
            
            schema.put("items", items);
            generationConfig.put("responseSchema", schema);
            requestBody.put("generationConfig", generationConfig);

            // HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Sending request to Gemini API...");
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            
            if (response == null) {
                throw new RuntimeException("Empty response from Gemini API");
            }

            // Extract the generated JSON string
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("No candidates returned from Gemini API");
            }
            
            Map<String, Object> candidate = candidates.get(0);
            Map<String, Object> contentNode = (Map<String, Object>) candidate.get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) contentNode.get("parts");
            if (parts == null || parts.isEmpty()) {
                throw new RuntimeException("No parts returned from Gemini API candidates");
            }
            
            String jsonText = (String) parts.get(0).get("text");
            log.info("Received response from Gemini API: {}", jsonText);

            return objectMapper.readValue(jsonText, new TypeReference<List<CRMRecord>>() {});

        } catch (Exception e) {
            log.error("Error communicating with Gemini API, falling back to mock processing: ", e);
            return generateMockResponse(rawBatch);
        }
    }

    private String getApiKey() {
        if (configuredApiKey != null && !configuredApiKey.trim().isEmpty()) {
            return configuredApiKey;
        }
        return System.getenv("GEMINI_API_KEY");
    }

    private List<CRMRecord> generateMockResponse(List<Map<String, String>> rawBatch) {
        log.info("Mock processing batch of size {}", rawBatch.size());
        List<CRMRecord> records = new ArrayList<>();
        
        for (Map<String, String> raw : rawBatch) {
            // Find key matches case-insensitively
            String rawName = getValue(raw, "name", "full name", "first name", "contact name");
            String rawEmail = getValue(raw, "email", "email address", "mail");
            String rawPhone = getValue(raw, "phone", "phone number", "telephone", "mobile");
            String rawCompany = getValue(raw, "company", "company name", "organization", "employer");
            String rawStatus = getValue(raw, "status", "lead status", "type");

            List<String> errors = new ArrayList<>();
            boolean valid = true;

            // Simple validation rules
            if (rawName == null || rawName.trim().isEmpty()) {
                errors.add("Name is required");
                valid = false;
            }
            if (rawEmail == null || !rawEmail.contains("@")) {
                errors.add("Email address is invalid");
                valid = false;
            }

            // Simple cleaning rules
            String name = rawName != null ? capitalize(rawName.trim()) : "";
            String email = rawEmail != null ? rawEmail.trim().toLowerCase() : "";
            String phone = rawPhone != null ? cleanPhone(rawPhone) : "";
            String company = rawCompany != null ? capitalize(rawCompany.trim()) : "";
            String status = rawStatus != null ? mapStatus(rawStatus) : "Lead";

            StringBuilder notes = new StringBuilder("Mock AI Processing. ");
            if (valid) {
                notes.append("Cleaned fields. ");
                if (!phone.equals(rawPhone)) notes.append("Formatted phone. ");
                if (!email.equals(rawEmail)) notes.append("Lowercased email. ");
            } else {
                notes.append("Failed validation: ").append(String.join(", ", errors));
            }

            records.add(CRMRecord.builder()
                    .name(name)
                    .email(email)
                    .phone(phone)
                    .company(company)
                    .status(status)
                    .originalName(rawName != null ? rawName : "")
                    .originalEmail(rawEmail != null ? rawEmail : "")
                    .originalPhone(rawPhone != null ? rawPhone : "")
                    .originalCompany(rawCompany != null ? rawCompany : "")
                    .originalStatus(rawStatus != null ? rawStatus : "")
                    .notes(notes.toString().trim())
                    .valid(valid)
                    .errors(errors)
                    .build());
        }

        return records;
    }

    private String getValue(Map<String, String> map, String... keys) {
        for (String k : keys) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(k)) {
                    return entry.getValue();
                }
            }
        }
        // Fallback to first non-empty value if no key matched (or return first key)
        return map.values().stream().findFirst().orElse("");
    }

    private String capitalize(String str) {
        if (str.isEmpty()) return str;
        String[] words = str.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)))
                  .append(w.substring(1).toLowerCase())
                  .append(" ");
            }
        }
        return sb.toString().trim();
    }

    private String cleanPhone(String phone) {
        String cleaned = phone.replaceAll("[^0-9+]", "");
        if (!cleaned.startsWith("+") && cleaned.length() >= 10) {
            cleaned = "+1" + cleaned; // Assume US prefix for mock
        }
        return cleaned;
    }

    private String mapStatus(String status) {
        String s = status.trim().toLowerCase();
        if (s.contains("lead") || s.contains("new")) return "Lead";
        if (s.contains("contact") || s.contains("customer")) return "Contact";
        if (s.contains("prospect") || s.contains("active")) return "Prospect";
        if (s.contains("inactive") || s.contains("cold") || s.contains("left")) return "Inactive";
        return "Lead";
    }
}
