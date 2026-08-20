const BASE_URL = 'http://localhost:8083/api';
const API_KEY = 'YOUR_SECRET_API_KEY_HERE';
const USER_ID = 101;

const getHeaders = () => ({
    'Content-Type': 'application/json',
    'X-API-KEY': API_KEY
});

document.addEventListener('DOMContentLoaded', () => {
    loadCart();
});

async function loadCart() {
    try {
        const response = await fetch(`${BASE_URL}/cart/${USER_ID}`, {
            headers: getHeaders()
        });

        if (!response.ok) throw new Error('Failed to fetch cart');

        const cartData = await response.json();
        renderCart(cartData);
    } catch (error) {
        console.error('Error loading cart:', error);
        showToast('Could not load cart data', 'error');
    }
}

function renderCart(cart) {
    const tableBody = document.getElementById('cartTableBody');
    const emptyMsg = document.getElementById('emptyCartMsg');
    const totalAmountElem = document.getElementById('totalAmount');
    const checkoutBtn = document.getElementById('checkoutBtn');

    tableBody.innerHTML = '';

    const items = cart.items || cart || [];

    if (!Array.isArray(items) || items.length === 0) {
        emptyMsg.classList.remove('hidden');
        totalAmountElem.innerText = 'LKR 0.00';
        checkoutBtn.disabled = false;
        return;
    }

    emptyMsg.classList.add('hidden');
    checkoutBtn.disabled = false;

    let totalCalculated = 0;

    items.forEach(item => {
        const itemPrice = Number(item.price) || 0;
        const itemQty = Number(item.quantity) || 1;
        const subtotal = itemPrice * itemQty;
        totalCalculated += subtotal;

        const row = document.createElement('tr');
        row.innerHTML = `
            <td><strong>${item.bookTitle || 'Book ID: ' + item.bookId}</strong></td>
            <td>LKR ${itemPrice.toFixed(2)}</td>
            <td>
                <input type="number" min="1" value="${itemQty}" 
                       class="qty-input" 
                       onchange="updateQuantity(${item.bookId}, this.value)">
            </td>
            <td>LKR ${subtotal.toFixed(2)}</td>
            <td>
                <button class="btn-delete" onclick="removeItem(${item.bookId})">Remove</button>
            </td>
        `;
        tableBody.appendChild(row);
    });

    const finalTotal = cart.totalAmount !== undefined ? cart.totalAmount : totalCalculated;
    totalAmountElem.innerText = `LKR ${Number(finalTotal).toFixed(2)}`;
}

async function updateQuantity(bookId, quantity) {
    if (quantity < 1) return;
    try {
        const response = await fetch(`${BASE_URL}/cart/update`, {
            method: 'PUT',
            headers: getHeaders(),
            body: JSON.stringify({ userId: USER_ID, bookId: bookId, quantity: parseInt(quantity) })
        });
        if (response.ok) {
            loadCart();
            showToast('Cart updated', 'success');
        }
    } catch (error) {
        console.error('Error updating quantity:', error);
    }
}

async function removeItem(bookId) {
    try {
        const response = await fetch(`${BASE_URL}/cart/remove?userId=${USER_ID}&bookId=${bookId}`, {
            method: 'DELETE',
            headers: getHeaders()
        });
        if (response.ok) {
            loadCart();
            showToast('Item removed from cart', 'success');
        }
    } catch (error) {
        console.error('Error removing item:', error);
    }
}

function goToCheckout() {
    window.location.href = 'checkout.html';
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