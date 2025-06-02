import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';

const PrivateRoute = ({ allowedRoles }) => {
  const token = localStorage.getItem('jwtToken');
  const userRole = localStorage.getItem('userRole');

  // 1. Check for authentication token
  if (!token) {
    // If no token, redirect to login page
    return <Navigate to="/login" replace />;
  }

  // 2. Check for roles if specified
  if (allowedRoles && allowedRoles.length > 0) {
    if (!userRole || !allowedRoles.includes(userRole)) {
      // If roles are specified and user doesn't have an allowed role, redirect to forbidden page
      return <Navigate to="/forbidden" replace />;
    }
  }

  // 3. If authenticated and roles match (or no roles specified), render the child routes/component
  // Outlet is used when PrivateRoute wraps other <Route> components
  // If PrivateRoute is used directly on an element prop, you'd render 'children' or the passed component.
  return <Outlet />;
};

export default PrivateRoute;