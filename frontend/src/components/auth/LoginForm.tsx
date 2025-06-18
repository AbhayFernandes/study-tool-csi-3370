import React, { useState } from 'react';

interface LoginFormProps {
  onLogin: (username: string, password: string) => void;
  loading: boolean;
  error: string;
}

const LoginForm: React.FC<LoginFormProps> = ({ onLogin, loading, error }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onLogin(username, password);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-background">
      <form
        onSubmit={handleSubmit}
        className="bg-card p-8 rounded-lg shadow-lg w-full max-w-sm flex flex-col gap-4 border border-border"
      >
        <h1 className="text-2xl font-bold text-center text-foreground mb-4">Study Tool Login</h1>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          className="px-4 py-3 rounded bg-input text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring border border-border"
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          className="px-4 py-3 rounded bg-input text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring border border-border"
        />
        <button
          type="submit"
          disabled={loading}
          className={`w-full py-3 rounded font-semibold transition-colors ${loading ? 'bg-muted text-muted-foreground cursor-not-allowed' : 'bg-primary hover:bg-primary/90 text-primary-foreground'}`}
        >
          {loading ? 'Logging in...' : 'Login'}
        </button>
        {error && (
          <div className="bg-destructive text-destructive-foreground rounded p-2 text-center text-sm">{error}</div>
        )}
        <div className="mt-4 text-xs text-muted-foreground text-center">
          <div><strong>Test Credentials:</strong></div>
          <div>Username: testuser</div>
          <div>Password: password123</div>
          <div className="mt-1 opacity-80">Or try: admin / admin123</div>
        </div>
      </form>
    </div>
  );
};

export default LoginForm; 