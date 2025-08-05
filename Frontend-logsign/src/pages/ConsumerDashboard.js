import React, { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import './Dashboard.css'; // Your existing CSS for dashboard layout
import './Modal.css';   // CSS for the new modal (or add styles to Dashboard.css)

function ConsumerDashboard() {
    const navigate = useNavigate();
    const location = useLocation();
    const [userName, setUserName] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [allProducts, setAllProducts] = useState([]);
    const [myOrders, setMyOrders] = useState([]);
    const [cartItems, setCartItems] = useState(null); // cartItems should be an object containing cartItems array

    // --- STATE FOR ADD TO CART MODAL ---
    const [showAddToCartModal, setShowAddToCartModal] = useState(false);
    const [productForModal, setProductForModal] = useState(null);
    const [modalQuantity, setModalQuantity] = useState('1'); // Quantity input in modal
    const [modalError, setModalError] = useState(null);      // Errors specific to the modal
    // --- END MODAL STATE ---

    const [currentView, setCurrentView] = useState('dashboard');

    // Function to fetch cart items
    const fetchCartItems = async (token) => {
        try {
            // No global setLoading(true) here, as this function might be called for a specific refresh.
            // If you want a loading indicator specific to the cart area, you'd need a separate state.
            const response = await fetch('http://localhost:8080/api/cart', {
                headers: { 'Authorization': `Bearer ${token}` },
            });

            if (!response.ok) {
                let errorMessage = 'Failed to fetch cart items.';
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch (e) {
                    errorMessage = `Server responded with status ${response.status}: ${await response.text()}`;
                }
                throw new Error(errorMessage);
            }
            const data = await response.json();
            // console.log("Fetched Cart Data:", data); // For debugging: check the structure
            setCartItems(data);
        } catch (err) {
            console.error('Error fetching cart items:', err);
            // Alert user about cart specific errors without overriding main page errors
            alert(`Could not refresh cart: ${err.message}`);
            // Optionally, set cartItems to null or an empty structure if fetch failed
            setCartItems(null);
        } finally {
            // This 'finally' block is useful if you had a specific cart-loading state
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
            setCurrentView('dashboard'); // Default view
        }

        const fetchDataForView = async () => {
            setLoading(true); // Global loading for the main view content
            setError(null);    // Clear previous errors

            const headers = { 'Authorization': `Bearer ${token}` };

            try {
                // Fetch products for 'products' or 'dashboard' view
                if (currentView === 'products' || currentView === 'dashboard') {
                    const productsRes = await fetch('http://localhost:8080/api/products', { headers });
                    if (!productsRes.ok) throw new Error('Failed to fetch products.');
                    const productsData = await productsRes.json();
                    setAllProducts(productsData);
                }

                // Fetch orders for 'orders' view
                if (currentView === 'orders') {
                    const ordersRes = await fetch('http://localhost:8080/api/orders/my', { headers });
                    if (!ordersRes.ok) throw new Error('Failed to fetch orders.');
                    const ordersData = await ordersRes.json();
                    setMyOrders(ordersData);
                }

                // Fetch cart items for 'cart' view, or initially to ensure cart data is ready
                // It's good to fetch cart items on initial load or navigation to dashboard/products too
                // if you want cart summary visible, but here it's tied to 'cart' view.
                // Given the original issue, calling it here on `currentView === 'cart'` is fine.
                if (currentView === 'cart') {
                    await fetchCartItems(token);
                }

            } catch (err) {
                console.error('Error fetching consumer dashboard data:', err);
                setError(err.message || 'An unexpected error occurred while loading consumer dashboard.');
            } finally {
                setLoading(false); // End global loading
            }
        };

        // Re-run this effect whenever `currentView` changes (due to URL param or internal state)
        // or `Maps` or `location.search` changes.
        fetchDataForView();

    }, [navigate, location.search, currentView]); // Dependency array

    // --- MODAL MANAGEMENT FUNCTIONS ---
    const openAddToCartDialog = (product) => {
        setProductForModal(product);
        setModalQuantity('1'); // Reset to default quantity
        setModalError(null);   // Clear previous errors
        setShowAddToCartModal(true);
    };

    const closeAddToCartDialog = () => {
        setShowAddToCartModal(false);
        setProductForModal(null); // Clear product data from modal
        setModalQuantity('1');    // Reset quantity
        setModalError(null);      // Clear any modal errors
    };

    const handleModalQuantityChange = (event) => {
        // Ensure input is a positive integer
        const value = event.target.value;
        if (value === '' || /^[1-9]\d*$/.test(value)) { // Allows empty string (for user typing) or positive integers
            setModalQuantity(value);
            if (modalError) { // Clear error when user starts typing
                setModalError(null);
            }
        }
    };
    // --- END MODAL MANAGEMENT FUNCTIONS ---

    // Handles the API call to add an item to the cart.
    const processAddToCartAPI = async (productId, quantity) => {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            alert('Please log in to add items to cart.');
            navigate('/login');
            return;
        }

        setLoading(true); // Use global loading for this action
        try {
            // Assuming backend expects productId and quantity as query parameters.
            // Consider sending as JSON body for POST requests for better practice.
            const url = `http://localhost:8080/api/cart/add?productId=${productId}&quantity=${quantity}`;
            const response = await fetch(url, {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` },
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
            closeAddToCartDialog(); // Close modal on success

            // --- FIX: ALWAYS REFRESH CART ITEMS AFTER SUCCESSFUL ADD ---
            await fetchCartItems(token); // Crucial: Fetch latest cart data

            // Optionally, refresh products list to show updated stock
            if (currentView === 'products' || currentView === 'dashboard') {
                 const headers = { 'Authorization': `Bearer ${token}` };
                 const productsRes = await fetch('http://localhost:8080/api/products', { headers });
                 if (productsRes.ok) {
                    const productsData = await productsRes.json();
                    setAllProducts(productsData);
                 }
            }
        } catch (err) {
            console.error('Error adding to cart:', err);
            setModalError(`Error: ${err.message}`); // Show error in the modal
        } finally {
            setLoading(false);
        }
    };

    // Handler for confirming add from modal
    const handleConfirmAddToCart = async () => {
        if (!productForModal) return;

        const token = localStorage.getItem('jwtToken');
        if (!token) {
            alert('Session expired. Please log in again.');
            closeAddToCartDialog();
            navigate('/login');
            return;
        }

        const quantityNum = parseInt(modalQuantity, 10);

        if (isNaN(quantityNum) || quantityNum <= 0) {
            setModalError('Please enter a valid quantity greater than 0.');
            return;
        }

        if (quantityNum > productForModal.quantity) {
            setModalError(`Required quantity (${quantityNum}) exceeds available stock (${productForModal.quantity}).`);
            return;
        }

        setModalError(null); // Clear error before proceeding with API call
        await processAddToCartAPI(productForModal.productId, quantityNum);
    };

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
        setError(null); // Clear main page error
        try {
            const response = await fetch(`http://localhost:8080/api/cart/items/${cartItemId}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
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
            await fetchCartItems(token); // Refresh cart items after removal
        } catch (err) {
            console.error('Error removing from cart:', err);
            setError(err.message || 'An error occurred while removing the item.'); // Set main page error
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateQuantity = async (cartItemId, newQuantityStr) => {
        const newQuantity = parseFloat(newQuantityStr); // Use parseFloat for number inputs

        if (isNaN(newQuantity) || newQuantity < 0) {
            alert('Please enter a valid quantity.');
            // Re-fetch to revert to last valid quantity if input is invalid
            const token = localStorage.getItem('jwtToken');
            if (token) await fetchCartItems(token);
            return;
        }

        if (newQuantity === 0) {
            if (window.confirm('Setting quantity to 0 will remove the item from your cart. Do you want to proceed?')) {
                handleRemoveFromCart(cartItemId); // Call remove function if user confirms
            } else {
                // If user cancels, re-fetch cart to revert the quantity visually
                const token = localStorage.getItem('jwtToken');
                if (token) await fetchCartItems(token);
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
        setError(null); // Clear main page error
        try {
            const url = `http://localhost:8080/api/cart/items/${cartItemId}?newQuantity=${newQuantity}`;
            const response = await fetch(url, {
                method: 'PUT',
                headers: { 'Authorization': `Bearer ${token}` }
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

            // alert('Cart item quantity updated!'); // Consider if alert is too frequent
            await fetchCartItems(token); // Refresh cart items after update
        } catch (err) {
            console.error('Error updating quantity:', err);
            setError(err.message || 'An error occurred while updating quantity.'); // Set main page error
            // On error, re-fetch cart to revert to last valid quantity if update failed
            const token = localStorage.getItem('jwtToken');
            if (token) await fetchCartItems(token);
        } finally {
            setLoading(false);
        }
    };

    /**
     * NEW: Handles the checkout process by calling the backend API.
     */
    const handleCheckout = async () => {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            alert('Session expired. Please log in again.');
            navigate('/login');
            return;
        }

        // Prevent checkout if cart is empty
        if (!cartItems || !cartItems.cartItems || cartItems.cartItems.length === 0) {
            alert("Your cart is empty. Please add items before checking out.");
            return;
        }

        setLoading(true); // Start loading for the checkout action
        setError(null);    // Clear previous errors

        try {
            const response = await fetch('http://localhost:8080/api/orders/place', {
                method: 'POST',
                headers: { 'Authorization': `Bearer ${token}` }
            });

            if (!response.ok) {
                let errorMessage = 'Failed to place order.';
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || errorMessage;
                } catch (e) {
                    errorMessage = `Server responded with status ${response.status}: ${await response.text()}`;
                }
                throw new Error(errorMessage);
            }

            const orderResponse = await response.json();
            console.log('Order placed successfully:', orderResponse);
            alert(`Order placed successfully! Your Order ID is: ${orderResponse.orderId}`);

            // On success, clear the local cart state and navigate to orders page
            setCartItems(null);
            handleNavigateView('orders'); // Redirect to orders to see the new order

        } catch (err) {
            console.error('Error placing order:', err);
            setError(err.message || 'An unexpected error occurred while placing your order.');
            alert(`Error: ${err.message}`); // Show an alert for immediate feedback
        } finally {
            setLoading(false); // End loading
        }
    };


    const handleLogout = () => {
        // FIX: Specific removal of items from localStorage
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('userName');
        // If you store other user-specific data, remove them here too.
        navigate('/login');
    };

    const handleNavigateView = (viewName) => {
        navigate(`/consumer-dashboard?view=${viewName}`);
    };

    // --- Conditional Rendering for Loading/Error States ---
    if (loading && !showAddToCartModal) {
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

    const isCartEmpty = !cartItems || !cartItems.cartItems || cartItems.cartItems.length === 0;

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
                            <button onClick={() => handleNavigateView('cart')}>View My Cart</button>
                        </div>
                    </>
                )}

                {currentView === 'products' && (
                    <>
                        <h3>Available Products</h3>
                        {loading && <p>Refreshing products...</p>}
                        <div className="product-list">
                            {allProducts.length === 0 && !loading ? (
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
                                        <p>₹{product.price.toFixed(2)} / {product.unit || 'unit'}</p>
                                        <p>Available: {product.quantity}</p>
                                        <button
                                            className="add-to-cart-button"
                                            onClick={() => openAddToCartDialog(product)}
                                            disabled={product.quantity <= 0 || loading}
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
                        {loading && <p>Loading orders...</p>}
                        {!loading && myOrders.length === 0 ? (
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

                {currentView === 'cart' && (
                    <>
                        <h3>My Cart</h3>
                        {/* More specific loading indicator for cart actions */}
                        {loading && <p>Loading cart...</p>}
                        {/* Ensure cartItems and cartItems.cartItems are not null/undefined and length > 0 */}
                        {!loading && !isCartEmpty ? (
                            <div className="cart-details">
                                <div className="cart-items-list">
                                    {cartItems.cartItems.map((item) => (
                                        <div className="cart-item-card" key={item.cartItemId}>
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
                                                    min="0" // Allow 0 to trigger removal
                                                    value={item.quantity}
                                                    onChange={(e) => handleUpdateQuantity(item.cartItemId, e.target.value)}
                                                    className="quantity-input"
                                                    disabled={loading}
                                                />
                                                <button
                                                    className="remove-button"
                                                    onClick={() => handleRemoveFromCart(item.cartItemId)}
                                                    disabled={loading}
                                                >
                                                    Remove
                                                </button>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                                <div className="cart-summary">
                                    <h3>Cart Summary</h3>
                                    <p>Total Items: {cartItems.cartItems.reduce((acc, item) => acc + item.quantity, 0)}</p>
                                    <h4>Total Amount: ₹{cartItems.totalAmount ? cartItems.totalAmount.toFixed(2) : '0.00'}</h4>
                                    <button
                                        className="checkout-button"
                                        onClick={handleCheckout}
                                        disabled={loading || isCartEmpty}
                                    >
                                        {loading ? 'Processing...' : 'Proceed to Checkout'}
                                    </button>
                                </div>
                            </div>
                        ) : (
                           // Only show "empty" message if not loading
                           !loading && <p>Your cart is empty. Start adding some fresh produce!</p>
                        )}
                    </>
                )}

                {currentView === 'messages' && (
                    <>
                        <h3>Messages</h3>
                        <p>Your messages with farmers or support.</p>
                        {/* Placeholder for messages functionality */}
                    </>
                )}

            </div> {/* end dashboard-content */}

            {/* --- ADD TO CART MODAL --- */}
            {showAddToCartModal && productForModal && (
                <div className="modal-overlay" onClick={closeAddToCartDialog}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <h2>Add to Cart</h2>
                        <img
                            src={productForModal.imageUrl ? `http://localhost:8080/api/products/images/${productForModal.imageUrl}` : "https://via.placeholder.com/100?text=No+Image"}
                            alt={productForModal.name}
                            className="modal-product-image"
                            onError={(e) => { e.target.onerror = null; e.target.src="https://via.placeholder.com/100?text=No+Image"; }}
                        />
                        <h3>{productForModal.name}</h3>
                        <p>Price: ₹{productForModal.price.toFixed(2)} / {productForModal.unit || 'unit'}</p>
                        <p><strong>Available Quantity: {productForModal.quantity}</strong></p>

                        <div className="modal-actions">
                            <label htmlFor="modal-quantity-input">Required Quantity:</label>
                            <input
                                type="number"
                                id="modal-quantity-input"
                                value={modalQuantity}
                                onChange={handleModalQuantityChange}
                                min="1"
                                max={productForModal.quantity}
                                className="quantity-input"
                                disabled={loading}
                            />
                            {modalError && <p className="modal-error-message">{modalError}</p>}
                            <button
                                onClick={handleConfirmAddToCart}
                                className="confirm-button"
                                disabled={loading || !modalQuantity || parseInt(modalQuantity) <= 0} // Disable if quantity is empty or invalid
                            >
                                {loading ? 'Adding...' : 'Confirm Add'}
                            </button>
                            <button
                                onClick={closeAddToCartDialog}
                                className="cancel-button"
                                disabled={loading}
                            >
                                Cancel
                            </button>
                        </div>
                    </div>
                </div>
            )}
            {/* --- END ADD TO CART MODAL --- */}

        </div> // end dashboard-container
    );
}

export default ConsumerDashboard;