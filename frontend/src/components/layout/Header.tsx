import React from 'react';

interface HeaderProps {
  username: string;
  onLogout: () => void;
}

const Header: React.FC<HeaderProps> = ({ username, onLogout }) => (
  <header className="w-full flex items-center justify-between px-6 py-4 bg-card border-b border-border">
    <div className="text-lg font-bold text-foreground">Study Tool Dashboard</div>
    <div className="flex items-center gap-4">
      <span className="text-primary font-medium">Welcome, {username}!</span>
      <button
        onClick={onLogout}
        className="px-4 py-2 bg-destructive hover:bg-destructive/90 text-destructive-foreground rounded transition-colors font-semibold"
      >
        Logout
      </button>
    </div>
  </header>
);

export default Header; 