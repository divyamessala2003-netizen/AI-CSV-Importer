# AI-powered CSV CRM Importer

An AI-driven CSV importing application built with **React** (frontend) and **Spring Boot** (backend) that cleans, validates, and normalizes messy contact lists into standardized CRM records using **Gemini 2.5 Flash**.

## Features

- **Drag and Drop CSV File Upload**: Sleek modern upload zone with drag over status and file validation.
- **Instant Preview**: Parses the uploaded CSV file locally and shows raw column headers and sample data.
- **Gemini AI Cleaning & Normalization**:
  - Auto-normalizes names (standard capitalization, splits/combines first and last names if needed).
  - Formats phone numbers to standard E.164 formats (e.g. `+11234567890`).
  - Standardizes emails (lowercases and trims whitespace).
  - Normalizes lead statuses into: `Lead`, `Contact`, `Prospect`, or `Inactive`.
  - Captures audit trace of original vs. corrected values.
- **Intelligent Validations**: Detects completely invalid emails or missing names, marking them as validation failures with reasons.
- **Glassmorphic UI**: High-end styling with interactive dashboard cards, custom dark theme, and grid filtering (All, Valid, Corrected, Errors).

---

## Setup & Running Guide

### Prerequisites
- **Java 21 JDK** installed.
- **Node.js** and **npm** installed.

---

### 1. Run the Backend (Spring Boot)

1. Open a terminal in `backend/` directory.
2. Configure your Gemini API Key. You can get one from Google AI Studio. Set it as an environment variable or edit `src/main/resources/application.properties`:
   ```bash
   # On Windows (PowerShell)
   $env:GEMINI_API_KEY="your-gemini-api-key-here"

   # On Windows (CMD)
   set GEMINI_API_KEY=your-gemini-api-key-here
   ```
   > [!NOTE]
   > If no `GEMINI_API_KEY` is provided, the backend will automatically fall back to **Mock AI validation & formatting** so the application works out-of-the-box!

3. Run the Spring Boot application using Maven:
   ```bash
   mvn spring-boot:run
   ```
   The backend will start and listen on port `8080`.

---

### 2. Run the Frontend (React + Vite)

1. Open a terminal in `frontend/` directory.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Launch the development server:
   ```bash
   npm run dev
   ```
   The frontend will start and open in your browser at `http://localhost:5173`.

---

## Sample CSV for Testing

Save the text below as `sample_messy_contacts.csv` and upload it to test the importer:

```csv
name,email,phone,company,status
john doe,JOHN.DOE@GMAIL.COM,(123) 456-7890,ACME CORP,new
jane smith,jane.smith.co.uk,+44 7911 123456,google inc.,active
,invalid-email-address,0000000000,test company,Inactive
Bob Johnson,bob@company.com,555-0199,MICROSOFT,cold
```

### What Gemini does during processing:
- **John Doe**: Standardizes name to `John Doe`, email to `john.doe@gmail.com`, phone to `+11234567890`, company to `Acme Corp`, and maps status `new` to `Lead`.
- **Jane Smith**: Marks email `jane.smith.co.uk` as invalid, naming the validation error ("Email address is invalid").
- **Third Row**: Fails validation because both `name` is missing and `email` is invalid.
- **Bob Johnson**: Normalizes status `cold` to `Inactive`.
