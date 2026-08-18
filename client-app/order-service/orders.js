const BASE_URL = 'http://localhost:8083/api';
const API_KEY = 'YOUR_SECRET_API_KEY_HERE';
const USER_ID = 101; 

let allAdminOrders = [];

const getHeaders = () => ({
    'Content-Type': 'application/json',
    'X-API-KEY': API_KEY
});

document.addEventListener('DOMContentLoaded', () => {
    loadUserOrders();
    loadAdminOrders();
});

function switchTab(tab) {
    const userTab = document.getElementById('userOrdersTab');
    const adminTab = document.getElementById('adminOrdersTab');
    const tabBtns = document.querySelectorAll('.tab-btn');

    if (tab === 'user') {
        userTab.classList.remove('hidden');
        adminTab.classList.add('hidden');
        tabBtns[0].classList.add('active');
        tabBtns[1].classList.remove('active');
        loadUserOrders();
    } else {
        adminTab.classList.remove('hidden');
        userTab.classList.add('hidden');
        tabBtns[1].classList.add('active');
        tabBtns[0].classList.remove('active');
        loadAdminOrders();
    }
}

// 1. Load User Orders directly from backend endpoint
async function loadUserOrders() {
    try {
        const response = await fetch(`${BASE_URL}/orders/user/${USER_ID}`, { headers: getHeaders() });
        if (!response.ok) throw new Error('Failed to fetch orders');

        const orders = await response.json();
        renderUserTable(orders);
    } catch (error) {
        console.error('Error fetching user orders:', error);
    }
}

function renderUserTable(orders) {
    const body = document.getElementById('userOrdersTableBody');
    const emptyMsg = document.getElementById('noUserOrders');
    body.innerHTML = '';

    if (!orders || orders.length === 0) {
        emptyMsg.classList.remove('hidden');
        return;
    }
    emptyMsg.classList.add('hidden');

    orders.forEach(order => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><strong>#${order.id}</strong></td>
            <td>LKR ${order.totalAmount ? order.totalAmount.toFixed(2) : '0.00'}</td>
            <td><span class="status-badge status-${(order.status || 'PENDING').toLowerCase()}">${order.status || 'PENDING'}</span></td>
        `;
        body.appendChild(row);
    });
}

// 2. Load All Orders for Admin
async function loadAdminOrders() {
    try {
        const response = await fetch(`${BASE_URL}/orders`, { headers: getHeaders() });
        if (!response.ok) throw new Error('Failed to fetch admin orders');

        allAdminOrders = await response.json();
        filterAdminOrders();
    } catch (error) {
        console.error('Error fetching admin orders:', error);
    }
}

function filterAdminOrders() {
    const selectedStatus = document.getElementById('statusFilter').value;
    let filtered = allAdminOrders;

    if (selectedStatus !== 'ALL') {
        filtered = allAdminOrders.filter(o => o.status === selectedStatus);
    }

    renderAdminTable(filtered);
}

function renderAdminTable(orders) {
    const body = document.getElementById('adminOrdersTableBody');
    const emptyMsg = document.getElementById('noAdminOrders');
    body.innerHTML = '';

    if (!orders || orders.length === 0) {
        emptyMsg.classList.remove('hidden');
        return;
    }
    emptyMsg.classList.add('hidden');

    orders.forEach(order => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><strong>#${order.id}</strong></td>
            <td>User ${order.userId}</td>
            <td>LKR ${order.totalAmount ? order.totalAmount.toFixed(2) : '0.00'}</td>
            <td><span class="status-badge status-${(order.status || 'PENDING').toLowerCase()}">${order.status || 'PENDING'}</span></td>
            <td>
                <select onchange="updateOrderStatus('${order.id}', ${order.userId}, ${order.totalAmount}, this.value)" class="status-select">
                    <option value="PENDING" ${order.status === 'PENDING' ? 'selected' : ''}>PENDING</option>
                    <option value="COMPLETED" ${order.status === 'COMPLETED' ? 'selected' : ''}>COMPLETED</option>
                    <option value="CANCELLED" ${order.status === 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                </select>
            </td>
            <td>
                <button onclick="deleteOrder('${order.id}')" class="btn-delete">Delete</button>
            </td>
        `;
        body.appendChild(row);
    });
}

// Update Order Status
async function updateOrderStatus(id, userId, totalAmount, newStatus) {
    const payload = { userId, totalAmount, status: newStatus };

    try {
        const response = await fetch(`${BASE_URL}/orders/${id}`, {
            method: 'PUT',
            headers: getHeaders(),
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            showToast('Order status updated!', 'success');
            loadAdminOrders();
        }
    } catch (error) {
        console.error('Error updating order:', error);
    }
}

// Delete Order
async function deleteOrder(id) {
    if (!confirm(`Are you sure you want to delete Order #${id}?`)) return;

    try {
        const response = await fetch(`${BASE_URL}/orders/${id}`, {
            method: 'DELETE',
            headers: getHeaders()
        });

        if (response.ok) {
            showToast('Order deleted', 'success');
            loadAdminOrders();
        }
    } catch (error) {
        console.error('Error deleting order:', error);
    }
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