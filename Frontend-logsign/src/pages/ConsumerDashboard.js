import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import './Dashboard.css'; // Your CSS for dashboard layout

function ConsumerDashboard() {
    const navigate = useNavigate();
    const location = useLocation();
    const [userName, setUserName] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [allProducts, setAllProducts] = useState([]);
    const [myOrders, setMyOrders] = useState([]);
    const [cartItems, setCartItems] = useState(null); // New state for cart items (null initially, or an empty object {})

    const [currentView, setCurrentView] = useState('dashboard');

    // Function to fetch cart items
    const fetchCartItems = async (token) => {
        try {
            setLoading(true); // Set loading to true while fetching cart
            const response = await fetch('http://localhost:8080/api/cart', {
                headers: { 'Authorization': `Bearer ${token}` },
            });

            if (!response.ok) {
                let errorMessage = 'Failed to fetch cart items.';
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch (e) {
                    errorMessage = await response.text();
                }
                throw new Error(errorMessage);
            }
            const data = await response.json();
            setCartItems(data); // Set the entire CartResponse object
        } catch (err) {
            console.error('Error fetching cart items:', err);
            setError(err.message || 'An unexpected error occurred while loading cart.');
        } finally {
            setLoading(false); // Set loading to false after fetching cart
        }
    };


    useEffect(() => {
        const token = localStorage.getItem('jwtToken');
        const userRole = localStorage.getItem('userRole');
        const storedUserName = localStorage.getItem('userName');

        if (!token || userRole !== 'CONSUMER') {
            navigate('/login');
            return;
        }

        setUserName(storedUserName || 'Consumer');

        const params = new URLSearchParams(location.search);
        const viewParam = params.get('view');

        const allowedViews = ['dashboard', 'products', 'orders', 'cart', 'messages'];
        if (viewParam && allowedViews.includes(viewParam)) {
            setCurrentView(viewParam);
        } else {
            setCurrentView('dashboard');
        }

        const fetchDataForView = async () => {
            setLoading(true);
            setError(null);
            const headers = { 'Authorization': `Bearer ${token}` };

            try {
                if (currentView === 'products' || currentView === 'dashboard') {
                    const productsRes = await fetch('http://localhost:8080/api/products', { headers });
                    if (!productsRes.ok) throw new Error('Failed to fetch products.');
                    const productsData = await productsRes.json();
                    setAllProducts(productsData);
                }

                if (currentView === 'orders') {
                    const ordersRes = await fetch('http://localhost:8080/api/orders/my', { headers });
                    if (!ordersRes.ok) throw new Error('Failed to fetch orders.');
                    const ordersData = await ordersRes.json();
                    setMyOrders(ordersData);
                }

                if (currentView === 'cart') {
                    await fetchCartItems(token); // Call the dedicated cart fetcher
                }

            } catch (err) {
                console.error('Error fetching consumer dashboard data:', err);
                setError(err.message || 'An unexpected error occurred while loading consumer dashboard.');
            } finally {
                setLoading(false);
            }
        };

        fetchDataForView();

    }, [navigate, location.search, currentView]); // currentView is now also a dependency

    const handleAddToCart = async (productId, quantity = 1) => {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            alert('Please log in to add items to cart.');
            navigate('/login');
            return;
        }

        try {
            const url = `http://localhost:8080/api/cart/add?productId=${productId}&quantity=${quantity}`;

            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
            });

            if (!response.ok) {
                let errorMessage = 'Failed to add product to cart.';
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch (jsonError) {
                    errorMessage = `Server responded with status ${response.status}: ${await response.text()}`;
                }
                throw new Error(errorMessage);
            }

            alert('Product added to cart!');
            // After adding, refresh the cart view if currently in cart view
            if (currentView === 'cart') {
                await fetchCartItems(token);
            }
            // Optionally, provide more direct visual feedback than an alert
        } catch (err) {
            console.error('Error adding to cart:', err);
            alert(`Error adding to cart: ${err.message}`);
        }
    };

    // --- NEW: Remove From Cart Handler ---
    const handleRemoveFromCart = async (cartItemId) => {
        if (!window.confirm('Are you sure you want to remove this item from your cart?')) {
            return;
        }
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            alert('Authentication missing. Please log in.');
            navigate('/login');
            return;
        }
        setLoading(true);
        setError(null);
        try {
            const response = await fetch(`http://localhost:8080/api/cart/items/${cartItemId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                let errorMessage = 'Failed to remove item from cart.';
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch (e) {
                    errorMessage = await response.text();
                }
                throw new Error(errorMessage);
            }

            alert('Item removed from cart!');
            // Refresh cart items after successful deletion
            await fetchCartItems(token);
        } catch (err) {
            console.error('Error removing from cart:', err);
            setError(err.message || 'An error occurred while removing the item.');
        } finally {
            setLoading(false);
        }
    };

    // --- NEW: Update Cart Item Quantity Handler ---
    const handleUpdateQuantity = async (cartItemId, newQuantity) => {
        // Basic validation for quantity
        if (newQuantity <= 0) {
            if (window.confirm('Setting quantity to 0 will remove the item. Do you want to proceed?')) {
                handleRemoveFromCart(cartItemId);
            }
            return;
        }

        const token = localStorage.getItem('jwtToken');
        if (!token) {
            alert('Authentication missing. Please log in.');
            navigate('/login');
            return;
        }
        setLoading(true);
        setError(null);
        try {
            const url = `http://localhost:8080/api/cart/items/${cartItemId}?newQuantity=${newQuantity}`;
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    // 'Content-Type': 'application/json' // Not strictly needed if sending as query param
                }
                // No body, parameters are in the URL
            });

            if (!response.ok) {
                let errorMessage = 'Failed to update item quantity.';
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch (e) {
                    errorMessage = await response.text();
                }
                throw new Error(errorMessage);
            }

            alert('Cart item quantity updated!');
            // Refresh cart items after successful update
            await fetchCartItems(token);
        } catch (err) {
            console.error('Error updating quantity:', err);
            setError(err.message || 'An error occurred while updating quantity.');
        } finally {
            setLoading(false);
        }
    };


    const handleLogout = () => {
        localStorage.clear();
        navigate('/login');
    };

    const handleNavigateView = (viewName) => {
        navigate(`/consumer-dashboard?view=${viewName}`);
    };


    if (loading) {
        return (
            <div className="dashboard-container">
                <Sidebar role="CONSUMER" currentView={currentView} onNavigate={handleNavigateView} />
                <div className="dashboard-content">
                    <h2>Loading Consumer Dashboard...</h2>
                    <p>Fetching data for you.</p>
                </div>
            </div>
        );
    }

    if (error) {
        return (
            <div className="dashboard-container">
                <Sidebar role="CONSUMER" currentView={currentView} onNavigate={handleNavigateView} />
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
            <Sidebar role="CONSUMER" currentView={currentView} onNavigate={handleNavigateView} />
            <div className="dashboard-content">
                <div className="dashboard-header">
                    <h1>Welcome, {userName}! (Consumer Dashboard)</h1>
                    <button onClick={handleLogout} className="logout-button">Logout</button>
                </div>

                {currentView === 'dashboard' && (
                    <>
                        <p>This is your consumer-specific dashboard. Browse fresh produce and place your orders.</p>
                        <h3>Quick Links / Summary</h3>
                        <p>Welcome back! Here's a quick overview.</p>
                        <div className="quick-links">
                            <button onClick={() => handleNavigateView('products')}>View All Products</button>
                            <button onClick={() => handleNavigateView('orders')}>View My Orders</button>
                            <button onClick={() => handleNavigateView('cart')}>View My Cart</button> {/* Added Quick Link */}
                        </div>
                    </>
                )}

                {currentView === 'products' && (
                    <>
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
                                        <p>₹{product.price.toFixed(2)} / {product.unit || 'unit'}</p> {/* Added product.unit */}
                                        <p>Available: {product.quantity}</p>
                                        <button
                                            className="add-to-cart-button"
                                            onClick={() => handleAddToCart(product.productId)}
                                            disabled={product.quantity <= 0}
                                        >
                                            {product.quantity > 0 ? 'Add to Cart' : 'Out of Stock'}
                                        </button>
                                    </div>
                                ))
                            )}
                        </div>
                    </>
                )}

                {currentView === 'orders' && (
                    <>
                        <h3>My Orders</h3>
                        {myOrders.length === 0 ? (
                            <p>You have not placed any orders yet.</p>
                        ) : (
                            <div className="order-list">
                                {myOrders.map((order) => (
                                    <div className="order-card" key={order.orderId}>
                                        <h4>Order ID: {order.orderId}</h4>
                                        <p>Order Date: {new Date(order.orderDate).toLocaleDateString()}</p>
                                        <p>Total Amount: ₹{order.totalAmount.toFixed(2)}</p>
                                        <p>Status: {order.orderStatus}</p>
                                        <h5>Items:</h5>
                                        <ul>
                                            {order.orderItems.map((item) => (
                                                <li key={item.orderItemId}>
                                                    {item.productName} (x{item.quantity}) - ₹{(item.pricePerUnit * item.quantity).toFixed(2)}
                                                </li>
                                            ))}
                                        </ul>
                                    </div>
                                ))}
                            </div>
                        )}
                    </>
                )}

                {/* --- NEW: Cart Display Section --- */}
                {currentView === 'cart' && (
                    <>
                        <h3>My Cart</h3>
                        {cartItems && cartItems.cartItems && cartItems.cartItems.length > 0 ? (
                            <div className="cart-details">
                                <div className="cart-items-list">
                                    {cartItems.cartItems.map((item) => (
                                        <div className="cart-item-card" key={item.cartItemId}>
                                            {/* Assuming productImageUrl exists on cart item, if not, you'll need to fetch product details or adjust your DTO */}
                                            {item.productImageUrl && (
                                                <img
                                                    src={`http://localhost:8080/api/products/images/${item.productImageUrl}`}
                                                    alt={item.productName}
                                                    className="cart-item-image"
                                                    onError={(e) => { e.target.onerror = null; e.target.src="https://via.placeholder.com/80?text=No+Image"; }}
                                                />
                                            )}
                                            <div className="cart-item-info">
                                                <h4>{item.productName}</h4>
                                                <p>Price per unit: ₹{item.pricePerUnit.toFixed(2)}</p>
                                                <p>Subtotal: ₹{(item.pricePerUnit * item.quantity).toFixed(2)}</p>
                                            </div>
                                            <div className="cart-item-actions">
                                                <label htmlFor={`quantity-${item.cartItemId}`}>Quantity:</label>
                                                <input
                                                    type="number"
                                                    id={`quantity-${item.cartItemId}`}
                                                    min="1"
                                                    value={item.quantity}
                                                    onChange={(e) => handleUpdateQuantity(item.cartItemId, parseFloat(e.target.value))}
                                                    className="quantity-input"
                                                />
                                                <button
                                                    className="remove-button"
                                                    onClick={() => handleRemoveFromCart(item.cartItemId)}
                                                >
                                                    Remove
                                                </button>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                                <div className="cart-summary">
                                    <h3>Cart Summary</h3>
                                    <p>Total Items: {cartItems.cartItems.length}</p>
                                    <h4>Total Amount: ₹{cartItems.totalAmount ? cartItems.totalAmount.toFixed(2) : '0.00'}</h4>
                                    <button className="checkout-button">Proceed to Checkout</button>
                                </div>
                            </div>
                        ) : (
                            <p>Your cart is empty. Start adding some fresh produce!</p>
                        )}
                    </>
                )}
                {/* --- END NEW: Cart Display Section --- */}

                {currentView === 'messages' && (
                    <>
                        <h3>Messages</h3>
                        <p>Your messages with farmers or support.</p>
                    </>
                )}
            </div>
        </div>
    );
}

export default ConsumerDashboard;