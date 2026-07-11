import React from 'react';
import { Play } from 'lucide-react';

export default function PreviewTable({ headers, rows, onImportClick, isImporting }) {
  if (!headers || headers.length === 0) return null;

  return (
    <div className="glass-card" style={{ marginBottom: '2rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '1.25rem', fontWeight: 600, color: 'var(--color-text-main)' }}>
            CSV File Preview
          </h3>
          <p style={{ fontSize: '0.85rem', color: 'var(--color-text-muted)' }}>
            Showing first {rows.length} rows. Click "Process & Import" to validate records via Gemini AI.
          </p>
        </div>
        <button 
          className="btn btn-primary" 
          onClick={onImportClick}
          disabled={isImporting}
        >
          <Play size={16} /> Process & Import
        </button>
      </div>

      <div className="table-wrapper">
        <table className="custom-table">
          <thead>
            <tr>
              {headers.map((header, index) => (
                <th key={index}>{header}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {rows.map((row, rowIndex) => (
              <tr key={rowIndex}>
                {headers.map((header, colIndex) => (
                  <td key={colIndex}>{row[header] || ""}</td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
