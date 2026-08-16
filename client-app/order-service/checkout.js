const BASE_URL = 'http://localhost:8083/api';
const API_KEY = 'YOUR_SECRET_API_KEY_HERE';
const USER_ID = 101; 

let currentCartTotal = 0;

const getHeaders = () => ({
    'Content-Type': 'application/json',
    'X-API-KEY': API_KEY
});

document.addEventListener('DOMContentLoaded', () => {
    fetchCartSummary();
});

async function fetchCartSummary() {
    try {
        const response = await fetch(`${BASE_URL}/cart/${USER_ID}`, { headers: getHeaders() });
        if (!response.ok) throw new Error('Failed to fetch cart');

        const cart = await response.json();
        currentCartTotal = cart.totalAmount || 0;
        document.getElementById('checkoutTotal').innerText = `LKR ${currentCartTotal.toFixed(2)}`;
        
        if (currentCartTotal === 0) {
            document.getElementById('placeOrderBtn').disabled = true;
            showToast('Your cart is empty!', 'error');
        }
    } catch (error) {
        console.error('Error loading checkout summary:', error);
    }
}

async function handleCheckout(event) {
    event.preventDefault(); 

    const btn = document.getElementById('placeOrderBtn');
    btn.disabled = true;
    btn.innerText = 'Processing Order...';

    const orderPayload = {
        userId: USER_ID,
        totalAmount: currentCartTotal,
        status: 'PENDING'
    };

    try {
        const response = await fetch(`${BASE_URL}/orders`, {
            method: 'POST',
            headers: getHeaders(),
            body: JSON.stringify(orderPayload)
        });

        if (response.ok) {
            const createdOrder = await response.json();
            await clearCartAfterOrder();
            showSuccessModal(createdOrder.id, createdOrder.totalAmount);
        } else {
            showToast('Failed to place order. Try again.', 'error');
            btn.disabled = false;
            btn.innerText = 'Confirm & Place Order 📦';
        }
    } catch (error) {
        console.error('Checkout error:', error);
        showToast('Backend connection error!', 'error');
        btn.disabled = false;
        btn.innerText = 'Confirm & Place Order 📦';
    }
}

async function clearCartAfterOrder() {
    try {
        await fetch(`${BASE_URL}/cart/clear/${USER_ID}`, {
            method: 'DELETE',
            headers: getHeaders()
        });
    } catch (error) {
        console.error('Error clearing cart:', error);
    }
}

function showSuccessModal(orderId, total) {
    document.getElementById('modalOrderId').innerText = `#${orderId}`;
    document.getElementById('modalTotalAmount').innerText = `LKR ${total.toFixed(2)}`;
    document.getElementById('successModal').classList.remove('hidden');
}

function goToOrders() {
    window.location.href = 'orders.html';
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    if (!container) return;
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerText = message;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}