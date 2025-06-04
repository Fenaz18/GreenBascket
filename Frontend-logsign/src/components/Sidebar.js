import React, { useEffect, useState } from 'react';
import { NavLink, useNavigate, useLocation } from 'react-router-dom';
import './Sidebar.css';

const Sidebar = ({ role, onNavigate, currentView }) => {
  const navigate = useNavigate();
  const location = useLocation();
  // Initialize currentRole from prop or localStorage, with localStorage as fallback
  const [currentRole, setCurrentRole] = useState(role || localStorage.getItem('userRole'));

  // Update currentRole if the 'role' prop changes (e.g., when context changes)
  useEffect(() => {
    if (role) {
      setCurrentRole(role);
    }
  }, [role]);

  const handleLogout = () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    localStorage.removeItem('userRole');
    navigate('/login');
  };

  // This helper function now combines NavLink's isActive with your custom 'currentView' logic
  // It will make the link 'active' if it's the current route AND the current internal view
  const getNavLinkClass = (path, viewName) => {
    // Check if the current actual URL path matches the link's path (e.g., /consumer-dashboard)
    const isRouteActive = location.pathname === path;

    // Check if the internal view matches
    const isViewActive = currentView === viewName;

    // Apply 'active' class if both conditions are met
    if (isRouteActive && isViewActive) {
      return 'active';
    }
    // If not active, return undefined so other NavLink default styling applies
    return undefined;
  };

  return (
    <div className="sidebar-container">
      <div className="sidebar-header">
        <h3>GreenBasket</h3>
      </div>
      <nav className="sidebar-nav">
        <ul>
          {currentRole === 'FARMER' && (
            <>
              <li className="sidebar-section-title">Farmer Tools</li>
              <li><NavLink to="/farmer-dashboard" className={({ isActive }) => (isActive ? 'active' : undefined)}>Dashboard</NavLink></li>
              <li><NavLink to="/add-product" className={({ isActive }) => (isActive ? 'active' : undefined)}>Add Product</NavLink></li>
              <li><NavLink to="/my-products" className={({ isActive }) => (isActive ? 'active' : undefined)}>My Products</NavLink></li>
              <li><NavLink to="/farmer-orders" className={({ isActive }) => (isActive ? 'active' : undefined)}>View Orders</NavLink></li>
              <li><NavLink to="/farmer-messages" className={({ isActive }) => (isActive ? 'active' : undefined)}>Messages</NavLink></li>
            </>
          )}

          {currentRole === 'CONSUMER' && (
            <>
              <li className="sidebar-section-title">Consumer Hub</li>
              <li>
                <NavLink
                  to="/consumer-dashboard?view=dashboard" // Corrected path with query param
                  className={({ isActive }) => getNavLinkClass('/consumer-dashboard', 'dashboard')}
                >
                  Dashboard
                </NavLink>
              </li>
              <li>
                <NavLink
                  to="/consumer-dashboard?view=products" // Corrected path with query param
                  className={({ isActive }) => getNavLinkClass('/consumer-dashboard', 'products')}
                >
                  Browse Products
                </NavLink>
              </li>
              <li>
                <NavLink
                  to="/consumer-dashboard?view=orders" // Corrected path with query param
                  className={({ isActive }) => getNavLinkClass('/consumer-dashboard', 'orders')}
                >
                  My Orders
                </NavLink>
              </li>
              <li>
                <NavLink
                  to="/consumer-dashboard?view=cart" // Corrected path with query param
                  className={({ isActive }) => getNavLinkClass('/consumer-dashboard', 'cart')}
                >
                  My Cart
                </NavLink>
              </li>
              <li><NavLink to="/consumer-messages" className={({ isActive }) => (isActive ? 'active' : undefined)}>Messages</NavLink></li>
            </>
          )}

          {currentRole && (
            <>
              <li className="sidebar-section-title">Account</li>
              <li><NavLink to="/profile" className={({ isActive }) => (isActive ? 'active' : undefined)}>Profile Settings</NavLink></li>
              <li>
                <button onClick={handleLogout} className="sidebar-logout-button">Logout</button>
              </li>
            </>
          )}

          {!currentRole && (
            <>
              <li><NavLink to="/login" className={({ isActive }) => (isActive ? 'active' : undefined)}>Login</NavLink></li>
              <li><NavLink to="/signup" className={({ isActive }) => (isActive ? 'active' : undefined)}>Signup</NavLink></li>
            </>
          )}
        </ul>
      </nav>
    </div>
  );
};

export default Sidebar;