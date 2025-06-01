import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Sidebar from '../components/Sidebar'; // Assuming you have a Sidebar component
import './Dashboard.css'; // Your CSS for dashboard layout

function ConsumerDashboard() {
    const navigate = useNavigate();
    const [userName, setUserName] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [allProducts, setAllProducts] = useState([]); // State for all products available for consumers

    useEffect(() => {
        const token = localStorage.getItem('jwtToken');
        const userRole = localStorage.getItem('userRole');
        const storedUserName = localStorage.getItem('userName');

        if (!token || userRole !== 'CONSUMER') {
            // Redirect to login if not authenticated or not a consumer
            navigate('/login');
            return;
        }

        setUserName(storedUserName || 'Consumer'); // Set name from localStorage

        const fetchConsumerData = async () => {
            setLoading(true);
            setError(null);
            try {
                // Fetch all products available for consumers
                const productsRes = await fetch('http://localhost:8080/api/products', { // Assuming this endpoint gives all products
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
                setAllProducts(productsData);

                // You can add more consumer-specific API calls here (e.g., user's past orders)

            } catch (err) {
                console.error('Error fetching consumer dashboard data:', err);
                setError(err.message || 'An unexpected error occurred while loading consumer dashboard.');
                // Consider logging out or redirecting on severe errors
            } finally {
                setLoading(false);
            }
        };

        fetchConsumerData();

    }, [navigate]); // navigate is a dependency

    const handleLogout = () => {
        localStorage.clear(); // Clear all user data from localStorage
        navigate('/login'); // Redirect to login page
    };

    if (loading) {
        return (
            <div className="dashboard-container">
                <Sidebar role="CONSUMER" />
                <div className="dashboard-content">
                    <h2>Loading Consumer Dashboard...</h2>
                    <p>Fetching products for you.</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="dashboard-container">
                <Sidebar role="CONSUMER" />
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
            <Sidebar role="CONSUMER" /> {/* Pass the role to your sidebar component */}
            <div className="dashboard-content">
                <div className="dashboard-header">
                    <h1>Welcome, {userName}! (Consumer Dashboard)</h1>
                    <button onClick={handleLogout} className="logout-button">Logout</button>
                </div>

                <p>This is your consumer-specific dashboard. Browse fresh produce and place your orders.</p>

                <h3>Available Products</h3>
                <div className="product-list">
                    {allProducts.length === 0 ? (
                        <p>No products available at the moment. Please check back later!</p>
                    ) : (
                        allProducts.map((product) => (
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
                                <p>₹{product.price.toFixed(2)} / {product.unit}</p>
                                <p>Available: {product.quantity}</p>
                                {/* Add consumer-specific actions like "Add to Cart" */}
                                <button className="add-to-cart-button">Add to Cart</button>
                            </div>
                        ))
                    )}
                </div>

                {/* Add more consumer-specific sections here (e.g., "My Orders", "Wishlist") */}
            </div>
        </div>
    );
}

export default ConsumerDashboard;