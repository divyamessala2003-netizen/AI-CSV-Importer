import React, { useState } from 'react';
import UploadBox from '../components/UploadBox';
import PreviewTable from '../components/PreviewTable';
import ResultTable from '../components/ResultTable';
import SummaryCard from '../components/SummaryCard';
import Loader from '../components/Loader';
import { previewCsvFile, importCsvFile } from '../services/api';
import { Sparkles, ArrowLeft } from 'lucide-react';

export default function Home() {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [preview, setPreview] = useState(null); // { headers: [], rows: [] }
  const [results, setResults] = useState(null); // { totalRecords, successfulRecords, correctedRecords, failedRecords, records: [] }

  const handleFileSelected = async (selectedFile) => {
    setFile(selectedFile);
    setError(null);
    setPreview(null);
    setResults(null);
    setLoading(true);

    try {
      const data = await previewCsvFile(selectedFile);
      setPreview(data);
    } catch (err) {
      console.error(err);
      setError("Failed to preview the CSV file. Please make sure it's a valid CSV format.");
      setFile(file); // Keep the file but show error
    } finally {
      setLoading(false);
    }
  };

  const handleImport = async () => {
    if (!file) return;
    setLoading(true);
    setError(null);

    try {
      const data = await importCsvFile(file);
      setResults(data);
      setPreview(null); // Clear preview when results arrive
    } catch (err) {
      console.error(err);
      setError("An error occurred during AI processing. Please check if the backend is running and GEMINI_API_KEY is configured.");
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setFile(null);
    setPreview(null);
    setResults(null);
    setError(null);
  };

  return (
    <div className="container">
      {/* Decorative Glow Background elements */}
      <div className="bg-glow-1"></div>
      <div className="bg-glow-2"></div>

      <header>
        <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', background: 'rgba(124, 77, 255, 0.1)', color: 'var(--color-primary)', padding: '0.4rem 1rem', borderRadius: '9999px', fontSize: '0.85rem', fontWeight: 600, marginBottom: '1.25rem', fontFamily: 'var(--font-heading)' }}>
          <Sparkles size={14} /> AI-Powered Data Normalizer
        </div>
        <h1>AI CSV Importer</h1>
        <p>
          Drop your messy customer CSV file. Gemini AI will automatically clean names, format emails and phone numbers, map statuses, and output compliant CRM records.
        </p>
      </header>

      {error && (
        <div className="glass-card" style={{ borderLeft: '4px solid var(--color-error)', background: 'rgba(239, 68, 68, 0.05)', marginBottom: '2rem', padding: '1.25rem 1.5rem', display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <span style={{ fontSize: '1.5rem', color: 'var(--color-error)' }}>⚠️</span>
          <div>
            <h4 style={{ color: 'var(--color-text-main)', fontWeight: 600, fontSize: '0.95rem' }}>Processing Error</h4>
            <p style={{ color: 'var(--color-text-muted)', fontSize: '0.85rem', marginTop: '0.2rem' }}>{error}</p>
          </div>
        </div>
      )}

      {loading && <Loader />}

      {!loading && !results && (
        <UploadBox 
          onFileSelected={handleFileSelected} 
          selectedFile={file} 
          onClear={handleClear} 
        />
      )}

      {!loading && preview && (
        <PreviewTable 
          headers={preview.headers} 
          rows={preview.rows} 
          onImportClick={handleImport}
          isImporting={loading}
        />
      )}

      {!loading && results && (
        <>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.5rem' }}>
            <button className="btn btn-secondary" onClick={handleClear} style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', padding: '0.5rem 1rem', fontSize: '0.85rem' }}>
              <ArrowLeft size={16} /> Import New File
            </button>
          </div>

          <SummaryCard 
            total={results.totalRecords}
            success={results.successfulRecords}
            corrected={results.correctedRecords}
            failed={results.failedRecords}
          />

          <ResultTable records={results.records} />
        </>
      )}
    </div>
  );
}
