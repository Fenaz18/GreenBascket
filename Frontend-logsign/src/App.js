// App.js (remains the same as your provided App.js)
import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

// Import your authentication components
import SignupPage from './components/SignupPage';
import LoginPage from './components/LoginPage';
import HomePage from './components/HomePage'; // Your general home/landing page

// Import your role-specific dashboard components
import FarmerDashboard from './pages/FarmerDashboard';
import ConsumerDashboard from './pages/ConsumerDashboard';

// Import other protected pages
import AddProductPage from './pages/AddProductPage';
import MyProductsPage from './pages/MyProductsPage';
import ProfilePage from './pages/ProfilePage';

// --- PrivateRoute Component ---
// This component checks both authentication (JWT) and role for route access.
const PrivateRoute = ({ children, allowedRoles }) => {
  const jwtToken = localStorage.getItem('jwtToken');
  const userRole = localStorage.getItem('userRole'); // Get the stored role

  // 1. Check if authenticated
  if (!jwtToken) {
    // If no token, user is not logged in, redirect to login page.
    return <Navigate to="/login" replace />;
  }

  // 2. Check if the user's role is allowed for this route
  if (allowedRoles && !allowedRoles.includes(userRole)) {
    // If the user's role is not in the allowedRoles for this route:
    console.warn(`Access Denied: User role '${userRole}' not allowed for this route.`);
    return <Navigate to="/login" replace />; // Or navigate to their allowed dashboard if you have a default
  }

  // If authenticated and role is allowed, render the child components.
  return children;
};

// --- App Component ---
function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes - Accessible to everyone */}
        <Route path="/" element={<HomePage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/login" element={<LoginPage />} />

        {/* Protected Routes - Require Authentication and potentially specific roles */}

        {/* Farmer Dashboard */}
        <Route
          path="/farmer-dashboard"
          element={
            <PrivateRoute allowedRoles={['FARMER']}>
              <FarmerDashboard />
            </PrivateRoute>
          }
        />

        {/* Consumer Dashboard */}
        <Route
          path="/consumer-dashboard"
          element={
            <PrivateRoute allowedRoles={['CONSUMER']}>
              <ConsumerDashboard />
            </PrivateRoute>
          }
        />

        {/* Other Protected Pages (accessible to any authenticated user, or specific roles if needed) */}
        <Route
          path="/add-product"
          element={
            <PrivateRoute allowedRoles={['FARMER']}> {/* Only farmers can add products */}
              <AddProductPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/my-products"
          element={
            <PrivateRoute allowedRoles={['FARMER']}> {/* Only farmers can view their products */}
              <MyProductsPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <PrivateRoute allowedRoles={['FARMER', 'CONSUMER']}> {/* Both roles can view their profile */}
              <ProfilePage />
            </PrivateRoute>
          }
        />

        {/* Catch-all Route - Redirects any unmatched URL to the home page or login */}
        {/* Changed this from '/' to '/login' as per the user's implied need for auth-first behavior */}
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;