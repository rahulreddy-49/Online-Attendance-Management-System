import React from 'react';

export const Toast = ({ toast }) => {
  if (!toast) return null;

  return (
    <div className="toast-container">
      <div className={`toast-item ${toast.type}`}>
        <span>{toast.type === 'success' ? '✅' : '⚠️'}</span>
        <span>{toast.message}</span>
      </div>
    </div>
  );
};
