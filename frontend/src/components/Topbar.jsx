import React from 'react';
import { useAuth } from '../context/AuthContext';

export const Topbar = ({ title, onQuickAction, quickActionLabel }) => {
  const { logout } = useAuth();

  return (
    <header className="app-topbar">
      <div className="topbar-left">
        <h1 className="page-title">{title}</h1>
      </div>

      <div className="topbar-actions">
        {onQuickAction && quickActionLabel && (
          <button className="btn-action primary sm" onClick={onQuickAction}>
            {quickActionLabel}
          </button>
        )}
        <button className="btn-signout" onClick={logout}>
          <span>Sign Out</span>
          <span>🚪</span>
        </button>
      </div>
    </header>
  );
};
