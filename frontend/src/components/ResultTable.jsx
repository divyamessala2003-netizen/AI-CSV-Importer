import React, { useState } from 'react';
import { ShieldCheck, ShieldAlert, Sparkles, AlertCircle } from 'lucide-react';

export default function ResultTable({ records }) {
  const [filter, setFilter] = useState('all'); // 'all', 'valid', 'corrected', 'errors'

  if (!records || records.length === 0) return null;

  // Helper functions to check corrections
  const isCorrected = (clean, original) => {
    if (!clean || !original) return false;
    return clean.trim().toLowerCase() !== original.trim().toLowerCase();
  };

  const isPhoneCorrected = (clean, original) => {
    if (!clean || !original) return false;
    // Strip everything except digits to see if they differ, or if format was prettified
    return clean.trim() !== original.trim();
  };

  const filteredRecords = records.filter(r => {
    if (filter === 'valid') return r.valid;
    if (filter === 'errors') return !r.valid;
    if (filter === 'corrected') {
      return r.valid && (
        isCorrected(r.name, r.originalName) ||
        isCorrected(r.email, r.originalEmail) ||
        isPhoneCorrected(r.phone, r.originalPhone) ||
        isCorrected(r.company, r.originalCompany) ||
        isCorrected(r.status, r.originalStatus)
      );
    }
    return true;
  });

  return (
    <div className="glass-card" style={{ marginBottom: '2rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h3 style={{ fontFamily: 'var(--font-heading)', fontSize: '1.25rem', fontWeight: 600, color: 'var(--color-text-main)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Sparkles size={20} style={{ color: 'var(--color-secondary)' }} />
            AI-Processed Results
          </h3>
          <p style={{ fontSize: '0.85rem', color: 'var(--color-text-muted)' }}>
            Gemini has normalized and validated your CSV data. Hover over highlighted items to see original values.
          </p>
        </div>

        {/* Filter Badges */}
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button 
            className={`btn ${filter === 'all' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem', borderRadius: '8px' }}
            onClick={() => setFilter('all')}
          >
            All ({records.length})
          </button>
          <button 
            className={`btn ${filter === 'valid' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem', borderRadius: '8px' }}
            onClick={() => setFilter('valid')}
          >
            Valid ({records.filter(r => r.valid).length})
          </button>
          <button 
            className={`btn ${filter === 'corrected' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem', borderRadius: '8px' }}
            onClick={() => setFilter('corrected')}
          >
            Corrected ({
              records.filter(r => r.valid && (
                isCorrected(r.name, r.originalName) ||
                isCorrected(r.email, r.originalEmail) ||
                isPhoneCorrected(r.phone, r.originalPhone) ||
                isCorrected(r.company, r.originalCompany) ||
                isCorrected(r.status, r.originalStatus)
              )).length
            })
          </button>
          <button 
            className={`btn ${filter === 'errors' ? 'btn-primary' : 'btn-secondary'}`}
            style={{ padding: '0.4rem 0.8rem', fontSize: '0.8rem', borderRadius: '8px' }}
            onClick={() => setFilter('errors')}
          >
            Errors ({records.filter(r => !r.valid).length})
          </button>
        </div>
      </div>

      <div className="table-wrapper">
        <table className="custom-table">
          <thead>
            <tr>
              <th style={{ width: '80px', textAlign: 'center' }}>Validity</th>
              <th>Name</th>
              <th>Email</th>
              <th>Phone</th>
              <th>Company</th>
              <th>Status</th>
              <th>Notes / Errors</th>
            </tr>
          </thead>
          <tbody>
            {filteredRecords.length === 0 ? (
              <tr>
                <td colSpan="7" style={{ textAlign: 'center', padding: '2rem', color: 'var(--color-text-muted)' }}>
                  No records match the selected filter.
                </td>
              </tr>
            ) : (
              filteredRecords.map((r, i) => {
                const nameCorrected = isCorrected(r.name, r.originalName);
                const emailCorrected = isCorrected(r.email, r.originalEmail);
                const phoneCorrected = isPhoneCorrected(r.phone, r.originalPhone);
                const companyCorrected = isCorrected(r.company, r.originalCompany);
                const statusCorrected = isCorrected(r.status, r.originalStatus);

                return (
                  <tr key={i} style={!r.valid ? { background: 'rgba(239, 68, 68, 0.02)' } : {}}>
                    {/* Validity icon */}
                    <td style={{ textAlign: 'center' }}>
                      {r.valid ? (
                        <ShieldCheck size={20} style={{ color: 'var(--color-success)' }} title="Valid Record" />
                      ) : (
                        <ShieldAlert size={20} style={{ color: 'var(--color-error)' }} title="Invalid Record" />
                      )}
                    </td>

                    {/* Name */}
                    <td 
                      className={r.valid && nameCorrected ? 'cell-corrected tooltip-trigger' : !r.valid && (!r.name || r.name.trim() === '') ? 'cell-error' : ''}
                      title={nameCorrected ? `Original: "${r.originalName}"` : ''}
                    >
                      {r.name || <span style={{ color: 'var(--color-error)', fontStyle: 'italic' }}>Missing</span>}
                    </td>

                    {/* Email */}
                    <td 
                      className={r.valid && emailCorrected ? 'cell-corrected tooltip-trigger' : !r.valid && (!r.email || !r.email.includes('@')) ? 'cell-error' : ''}
                      title={emailCorrected ? `Original: "${r.originalEmail}"` : ''}
                    >
                      {r.email || <span style={{ color: 'var(--color-error)', fontStyle: 'italic' }}>Missing</span>}
                    </td>

                    {/* Phone */}
                    <td 
                      className={r.valid && phoneCorrected ? 'cell-corrected tooltip-trigger' : ''}
                      title={phoneCorrected ? `Original: "${r.originalPhone}"` : ''}
                    >
                      {r.phone || <span style={{ color: 'var(--color-text-dim)', fontStyle: 'italic' }}>N/A</span>}
                    </td>

                    {/* Company */}
                    <td 
                      className={r.valid && companyCorrected ? 'cell-corrected tooltip-trigger' : ''}
                      title={companyCorrected ? `Original: "${r.originalCompany}"` : ''}
                    >
                      {r.company || <span style={{ color: 'var(--color-text-dim)', fontStyle: 'italic' }}>N/A</span>}
                    </td>

                    {/* Status */}
                    <td 
                      className={r.valid && statusCorrected ? 'cell-corrected tooltip-trigger' : ''}
                      title={statusCorrected ? `Original: "${r.originalStatus}"` : ''}
                    >
                      <span className={`badge badge-${(r.status || 'lead').toLowerCase()}`}>
                        {r.status || 'Lead'}
                      </span>
                    </td>

                    {/* Notes/Errors */}
                    <td>
                      {r.valid ? (
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>
                          <span style={{ overflow: 'hidden', textOverflow: 'ellipsis' }}>{r.notes}</span>
                        </div>
                      ) : (
                        <div style={{ display: 'flex', alignItems: 'flex-start', gap: '0.4rem', fontSize: '0.8rem', color: 'var(--color-error)' }}>
                          <AlertCircle size={14} style={{ marginTop: '2px', flexShrink: 0 }} />
                          <div style={{ display: 'flex', flexDirection: 'column' }}>
                            {r.errors.map((err, errIdx) => (
                              <span key={errIdx}>{err}</span>
                            ))}
                          </div>
                        </div>
                      )}
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
