import React from 'react';
import { Database, CheckCircle, AlertTriangle, XCircle } from 'lucide-react';

export default function SummaryCard({ total, success, corrected, failed }) {
  return (
    <div className="summary-grid">
      <div className="glass-card summary-card-stat" style={{ borderLeft: '4px solid var(--color-primary)' }}>
        <div style={{ display: 'flex', justifyContent: 'center', color: 'var(--color-primary)', marginBottom: '0.5rem' }}>
          <Database size={24} />
        </div>
        <div className="stat-label">Total Uploaded</div>
        <div className="stat-value" style={{ color: 'var(--color-text-main)' }}>{total}</div>
      </div>

      <div className="glass-card summary-card-stat" style={{ borderLeft: '4px solid var(--color-success)' }}>
        <div style={{ display: 'flex', justifyContent: 'center', color: 'var(--color-success)', marginBottom: '0.5rem' }}>
          <CheckCircle size={24} />
        </div>
        <div className="stat-label">Valid Records</div>
        <div className="stat-value" style={{ color: 'var(--color-success)' }}>{success}</div>
      </div>

      <div className="glass-card summary-card-stat" style={{ borderLeft: '4px solid var(--color-warning)' }}>
        <div style={{ display: 'flex', justifyContent: 'center', color: 'var(--color-warning)', marginBottom: '0.5rem' }}>
          <AlertTriangle size={24} />
        </div>
        <div className="stat-label">AI Corrected</div>
        <div className="stat-value" style={{ color: 'var(--color-warning)' }}>{corrected}</div>
      </div>

      <div className="glass-card summary-card-stat" style={{ borderLeft: '4px solid var(--color-error)' }}>
        <div style={{ display: 'flex', justifyContent: 'center', color: 'var(--color-error)', marginBottom: '0.5rem' }}>
          <XCircle size={24} />
        </div>
        <div className="stat-label">Validation Errors</div>
        <div className="stat-value" style={{ color: 'var(--color-error)' }}>{failed}</div>
      </div>
    </div>
  );
}
