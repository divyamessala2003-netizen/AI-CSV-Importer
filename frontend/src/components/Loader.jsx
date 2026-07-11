import React, { useState, useEffect } from 'react';

const LOADING_PHASES = [
  'Uploading CSV file...',
  'Reading headers and columns...',
  'Invoking Gemini 2.5 Flash...',
  'Standardizing phone numbers to E.164...',
  'Validating email formats...',
  'Cleaning company and contact casing...',
  'Mapping status fields to CRM categories...',
  'Finalizing records configuration...'
];

export default function Loader() {
  const [phaseIndex, setPhaseIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setPhaseIndex((prevIndex) => (prevIndex + 1) % LOADING_PHASES.length);
    }, 2500);

    return () => clearInterval(interval);
  }, []);

  return (
    <div className="spinner-container">
      <div className="spinner"></div>
      <div className="loading-text">{LOADING_PHASES[phaseIndex]}</div>
      <p style={{ marginTop: '0.75rem', fontSize: '0.85rem', color: 'var(--color-text-muted)', textAlign: 'center' }}>
        This might take a moment depending on the number of records.
      </p>
    </div>
  );
}
