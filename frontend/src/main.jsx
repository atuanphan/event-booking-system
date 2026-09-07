import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import App from './App';
import { AuthProvider } from './context/AuthContext';
import './index.css';

function ErrorFallback({ error }) {
  return (
    <div style={{ padding: 40, fontFamily: 'monospace' }}>
      <h1 style={{ color: 'red' }}>Runtime Error</h1>
      <pre style={{ whiteSpace: 'pre-wrap', background: '#f5f5f5', padding: 16, borderRadius: 8 }}>
        {error?.message}
        {'\n\n'}
        {error?.stack}
      </pre>
    </div>
  );
}

class ErrorBoundary extends React.Component {
  state = { error: null };
  static getDerivedStateFromError(error) { return { error }; }
  render() {
    if (this.state.error) return <ErrorFallback error={this.state.error} />;
    return this.props.children;
  }
}

ReactDOM.createRoot(document.getElementById('root')).render(
  <ErrorBoundary>
    <BrowserRouter>
      <AuthProvider>
        <App />
        <Toaster position="top-right" autoClose={3000} />
      </AuthProvider>
    </BrowserRouter>
  </ErrorBoundary>
);
