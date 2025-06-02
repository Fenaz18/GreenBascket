// src/pages/ForbiddenPage.js
import React from 'react';
import { useNavigate } from 'react-router-dom';

const ForbiddenPage = () => {
  const navigate = useNavigate();

  const handleGoToLogin = () => {
    // Clear any potentially lingering tokens or user info if access was denied due to session issues
    localStorage.clear();
    navigate('/login');
  };

  return (
    <div style={{
      textAlign: 'center',
      padding: '50px',
      backgroundColor: '#f8d7da',
      border: '1px solid #f5c6cb',
      borderRadius: '8px',
      margin: '50px auto',
      maxWidth: '600px',
      boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)'
    }}>
      <h1 style={{ color: '#dc3545', marginBottom: '20px' }}>Access Denied!</h1>
      <p style={{ color: '#721c24', fontSize: '1.1em', lineHeight: '1.5' }}>
        You do not have the necessary permissions to view this page.
        Please ensure you are logged in with the correct account role.
      </p>
      <button
        onClick={handleGoToLogin}
        style={{
          backgroundColor: '#007bff',
          color: 'white',
          padding: '12px 25px',
          border: 'none',
          borderRadius: '5px',
          cursor: 'pointer',
          fontSize: '1em',
          marginTop: '30px',
          transition: 'background-color 0.3s ease'
        }}
      >
        Go to Login
      </button>
    </div>
  );
};

export default ForbiddenPage;