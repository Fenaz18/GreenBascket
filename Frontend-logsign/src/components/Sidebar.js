import React, { useEffect, useState } from 'react';
import { NavLink, useNavigate, useLocation } from 'react-router-dom';
import './Sidebar.css';

// Receive onNavigate callback and currentView state from parent
const Sidebar = ({ role, onNavigate, currentView }) => { // <-- Added onNavigate and currentView props
  const navigate = useNavigate();
  const location = useLocation();
  const [currentRole, setCurrentRole] = useState(role || localStorage.getItem('userRole'));

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

  // Helper to determine active link based on currentView from parent (ConsumerDashboard)
  const getLinkClass = (viewName) => {
    // For general dashboard routes that don't change the internal view,
    // you might keep `isActive` from NavLink.
    // For controlling the view within ConsumerDashboard, use `currentView`.
    if (viewName && currentView === viewName) {
      return 'active';
    }
    // Default NavLink isActive check for external routes or non-view-controlling links
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
                {/* Dashboard link will set currentView to 'dashboard' */}
                <NavLink
                  to="#" // No direct route change, controlled by state
                  className={getLinkClass('dashboard')}
                  onClick={() => onNavigate('dashboard')} // Call callback to update state
                >
                  Dashboard
                </NavLink>
              </li>
              <li>
                {/* Browse Products link will set currentView to 'products' */}
                <NavLink
                  to="#" // No direct route change, controlled by state
                  className={getLinkClass('products')}
                  onClick={() => onNavigate('products')} // Call callback to update state
                >
                  Browse Products
                </NavLink>
              </li>
              <li><NavLink to="/my-orders" className={({ isActive }) => (isActive ? 'active' : undefined)}>My Orders</NavLink></li>
              <li><NavLink to="/my-cart" className={({ isActive }) => (isActive ? 'active' : undefined)}>My Cart</NavLink></li>
              <li><NavLink to="/consumer-messages" className={({ isActive }) => (isActive ? 'active' : undefined)}>Messages</NavLink></li>
            </>
          )}

          {currentRole && (
            <>
              <li className="sidebar-section-title">Account</li>
              {/* Profile link remains a separate route */}
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