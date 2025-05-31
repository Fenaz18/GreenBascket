import React, { useEffect, useState } from 'react';
import Sidebar from '../components/Sidebar';
import './Dashboard.css';

const DashboardPage = () => {
  const [products, setProducts] = useState([]);
  const [user, setUser] = useState(null); // Initialize as null, will store fetched user data
  const [loading, setLoading] = useState(true); // Loading state
  const [error, setError] = useState(null); // Error state for API calls

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true); // Start loading
        setError(null); // Clear previous errors

        // 1. Fetch User Data (Example: from localStorage or an /api/user-profile endpoint)
        // In a real app, you'd get this from your authentication flow.
        // For now, let's simulate fetching it.
        const token = localStorage.getItem('jwtToken'); // Get token from localStorage
        if (!token) {
          setError('Authentication token not found. Please log in.');
          setLoading(false);
          // Redirect to login page if no token
          // navigate('/login');
          return;
        }

        // Example: Fetch user details if needed, or parse from token
        // For now, we'll keep the hardcoded user for demonstration, but it's not ideal.
        setUser({ role: 'FARMER', name: 'John Doe' });


        // 2. Fetch All Products
        const productsRes = await fetch('http://localhost:8080/api/products', {
          headers: {
            'Authorization': `Bearer ${token}` // Include JWT token for authenticated requests
          }
        });

        // Check if the product response was successful (HTTP status 2xx)
        if (!productsRes.ok) {
          // Attempt to parse error message if available, otherwise use status text
          const errorData = await productsRes.text(); // Use .text() to avoid JSON parsing errors for non-JSON responses
          throw new Error(`Failed to fetch products: ${productsRes.status} - ${errorData || productsRes.statusText}`);
        }

        const productsData = await productsRes.json();
        setProducts(productsData);

      } catch (err) {
        console.error('Error fetching dashboard data:', err);
        setError(err.message || 'An unexpected error occurred while loading dashboard.');
      } finally {
        setLoading(false); // End loading
      }
    };

    fetchDashboardData();
  }, []); // Empty dependency array means this runs once on mount

  if (loading) {
    return (
      <div className="dashboard-container">
        <Sidebar role={user ? user.role : ''} /> {/* Render sidebar even when loading */}
        <div className="dashboard-content">
          <h2>Loading Dashboard...</h2>
          <p>Please wait while we fetch your data.</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard-container">
        <Sidebar role={user ? user.role : ''} />
        <div className="dashboard-content">
          <h2 style={{ color: 'red' }}>Error:</h2>
          <p style={{ color: 'red' }}>{error}</p>
          <p>Please try again later or contact support.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      {/* Ensure user is not null before passing role to Sidebar */}
      <Sidebar role={user ? user.role : ''} />
      <div className="dashboard-content">
        <h2>Welcome, {user ? user.name : 'Guest'}</h2> {/* Handle user being null */}
        <h3>All Products</h3>
        <div className="product-list">
          {products.length === 0 ? (
            <p>No products available at the moment.</p> // Empty state
          ) : (
            products.map((product) => (
              <div className="product-card" key={product.productId}> {/* Use productId as key */}
                <h4>{product.name}</h4>
                <p>{product.description}</p>
                <p>₹{product.price}</p>
                {/* Add more product details as needed */}
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;