import React, { useState, useRef } from 'react';
import { UploadCloud, FileSpreadsheet, Trash2 } from 'lucide-react';

export default function UploadBox({ onFileSelected, selectedFile, onClear }) {
  const [dragActive, setDragActive] = useState(false);
  const inputRef = useRef(null);

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true);
    } else if (e.type === "dragleave") {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      const file = e.dataTransfer.files[0];
      if (file.name.endsWith('.csv')) {
        onFileSelected(file);
      } else {
        alert("Please upload a CSV file only.");
      }
    }
  };

  const handleChange = (e) => {
    e.preventDefault();
    if (e.target.files && e.target.files[0]) {
      onFileSelected(e.target.files[0]);
    }
  };

  const onButtonClick = () => {
    inputRef.current.click();
  };

  return (
    <div className="glass-card" style={{ marginBottom: '2rem' }}>
      {!selectedFile ? (
        <div 
          className={`upload-zone ${dragActive ? 'drag-active' : ''}`}
          onDragEnter={handleDrag}
          onDragOver={handleDrag}
          onDragLeave={handleDrag}
          onDrop={handleDrop}
          onClick={onButtonClick}
        >
          <input 
            ref={inputRef}
            type="file" 
            accept=".csv"
            style={{ display: 'none' }} 
            onChange={handleChange}
          />
          <UploadCloud className="upload-icon" size={48} />
          <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '1.25rem', marginBottom: '0.5rem', fontWeight: 600 }}>
            Drag and Drop your CSV
          </h3>
          <p style={{ color: 'var(--color-text-muted)', fontSize: '0.9rem', marginBottom: '1rem' }}>
            or click to browse your files
          </p>
          <span style={{ fontSize: '0.75rem', color: 'var(--color-text-dim)' }}>
            Only .csv files are supported
          </span>
        </div>
      ) : (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <div style={{ 
              background: 'rgba(0, 229, 255, 0.1)', 
              color: 'var(--color-secondary)',
              padding: '0.75rem',
              borderRadius: '12px',
              display: 'flex',
              alignItems: 'center'
            }}>
              <FileSpreadsheet size={28} />
            </div>
            <div>
              <h4 style={{ fontFamily: 'var(--font-heading)', fontSize: '1.05rem', fontWeight: 600, color: 'var(--color-text-main)' }}>
                {selectedFile.name}
              </h4>
              <p style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>
                {(selectedFile.size / 1024).toFixed(2)} KB
              </p>
            </div>
          </div>
          <button 
            className="btn btn-secondary" 
            onClick={onClear} 
            style={{ display: 'inline-flex', alignItems: 'center', gap: '0.5rem', color: 'var(--color-error)' }}
          >
            <Trash2 size={16} /> Remove
          </button>
        </div>
      )}
    </div>
  );
}
