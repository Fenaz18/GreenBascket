import React, { useEffect, useState } from 'react';
import Sidebar from '../components/Sidebar';
import './Dashboard.css'; // Make sure this CSS file is present
import { useNavigate } from 'react-router-dom'; // Added for redirection

const DashboardPage = () => {
  const navigate = useNavigate();

  const [products, setProducts] = useState([]);
  const [user, setUser] = useState(null); // Will store fetched user data (name, role, etc.)
  const [loading, setLoading] = useState(true); // Loading state for all data
  const [error, setError] = useState(null); // Error state for API calls

  useEffect(() => {
    const fetchDashboardData = async () => {
      setLoading(true); // Start loading for all data
      setError(null); // Clear previous errors

      const token = localStorage.getItem('jwtToken');
      // The userId is not strictly needed for /api/users/profile as backend uses @AuthenticationPrincipal
      // but it's good to have it in localStorage for other user-specific calls.
      // const userId = localStorage.getItem('userId');

      if (!token) {
        setError('Authentication token not found. Please log in.');
        setLoading(false);
        navigate('/login'); // Redirect to login if no token
        return;
      }

      try {
        // --- 1. Fetch User Profile Data ---
        const userProfileRes = await fetch('http://localhost:8080/api/users/profile', {
          headers: {
            'Authorization': `Bearer ${token}` // Include JWT token
          }
        });

        if (!userProfileRes.ok) {
          let errorMessage = 'Failed to fetch user profile.';
          try {
            const errorData = await userProfileRes.json();
            errorMessage = errorData.message || errorMessage;
          } catch (e) { /* ignore */ }

          if (userProfileRes.status === 401 || userProfileRes.status === 403) {
            errorMessage = 'Your session has expired or is invalid. Please log in again.';
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('userId'); // Clear userId too
            navigate('/login');
          }
          throw new Error(errorMessage);
        }

        const userProfileData = await userProfileRes.json();
        setUser(userProfileData); // Set the fetched user data

        // --- 2. Fetch All Products (or specific products based on role if needed) ---
        // For now, fetching all products as per your previous code.
        // If you want farmer-specific products, you'd use:
        // `http://localhost:8080/api/products/farmer/${userProfileData.userId}`
        const productsRes = await fetch('http://localhost:8080/api/products', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (!productsRes.ok) {
          let errorMessage = 'Failed to fetch products.';
          try {
            const errorData = await productsRes.json();
            errorMessage = errorData.message || errorMessage;
          } catch (e) { /* ignore */ }
          throw new Error(errorMessage);
        }

        const productsData = await productsRes.json();
        setProducts(productsData);

      } catch (err) {
        console.error('Error fetching dashboard data:', err);
        setError(err.message || 'An unexpected error occurred while loading dashboard.');
      } finally {
        setLoading(false); // End loading regardless of success or failure
      }
    };

    fetchDashboardData();
  }, [navigate]); // Add navigate to dependency array

  if (loading) {
    return (
      <div className="dashboard-container">
        {/* Pass a placeholder role or handle null user for Sidebar during loading */}
        <Sidebar role={user ? user.role : ''} />
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
          <button onClick={() => navigate('/login')} style={{ marginTop: '20px', padding: '10px 20px', backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '5px', cursor: 'pointer' }}>
            Go to Login
          </button>
        </div>
      </div>
    );
  }

  // Render content only if user data is available
  if (!user) {
    // This case should ideally be caught by the loading/error states,
    // but as a fallback, if loading is false and user is null, it's an issue.
    return (
      <div className="dashboard-container">
        <Sidebar role={''} />
        <div className="dashboard-content">
          <h2>User data not available.</h2>
          <p>Please try logging in again.</p>
          <button onClick={() => navigate('/login')}>Go to Login</button>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      {/* Pass the actual user role to Sidebar */}
      <Sidebar role={user.role} />
      <div className="dashboard-content">
        {/* Display the fetched user's name */}
        <h2>Welcome, {user.name}!</h2>
        <h3>All Products</h3> {/* Or "Your Products" if filtered by role */}
        <div className="product-list">
          {products.length === 0 ? (
            <p>No products available at the moment.</p>
          ) : (
            products.map((product) => (
              <div className="product-card" key={product.productId}>
                {product.imageUrl && (
                  <img
                    src={`http://localhost:8080/api/products/images/${product.imageUrl}`}
                    alt={product.name}
                    className="product-image"
                    onError={(e) => { e.target.onerror = null; e.target.src="https://via.placeholder.com/150?text=Image+Not+Found"; }}
                  />
                )}
                <h4>{product.name}</h4>
                <p>{product.description}</p>
                <p>₹{product.price.toFixed(2)}</p>
                <p>Quantity: {product.quantity}</p>
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