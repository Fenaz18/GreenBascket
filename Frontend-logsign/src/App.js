import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import SignupPage from './components/SignupPage';
import LoginPage from './components/LoginPage';
import HomePage from './components/HomePage';

import AddProductPage from './pages/AddProductPage';
import MyProductsPage from './pages/MyProductsPage';
import ProfilePage from './pages/ProfilePage';
import DashboardPage from './pages/DashboardPage'; // Optional

function App() {
  const isAuthenticated = !!localStorage.getItem('jwtToken');

  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<HomePage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/login" element={<LoginPage />} />

        {/* Protected Routes */}
        {isAuthenticated ? (
          <>
            <Route path="/add-product" element={<AddProductPage />} />
            <Route path="/my-products" element={<MyProductsPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/dashboard" element={<DashboardPage />} />
          </>
        ) : (
          <>
            <Route path="/add-product" element={<Navigate to="/login" />} />
            <Route path="/my-products" element={<Navigate to="/login" />} />
            <Route path="/profile" element={<Navigate to="/login" />} />
            <Route path="/dashboard" element={<Navigate to="/login" />} />
          </>
        )}

        {/* Catch-all Route */}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
