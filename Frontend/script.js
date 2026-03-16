const API_URL = 'http://localhost:8080';
let currentToken = localStorage.getItem('jwtToken');
let currentAccount = localStorage.getItem('accountNumber');
let currentOperation = null; // 'deposit' or 'withdraw'

// DOM Elements
const authOverlay = document.getElementById('auth-overlay');
const dashboard = document.getElementById('dashboard');
const loginForm = document.getElementById('login-form');
const registerForm = document.getElementById('register-form');
const loginTab = document.getElementById('login-tab');
const registerTab = document.getElementById('register-tab');
const balanceDisplay = document.getElementById('balance-amount');
const accountDisplay = document.getElementById('display-account');
const historyBody = document.getElementById('history-body');
const logoutBtn = document.getElementById('logout-btn');
const notification = document.getElementById('notification');

// Modal Elements
const modal = document.getElementById('operation-modal');
const modalTitle = document.getElementById('modal-title');
const modalAmountInput = document.getElementById('operation-amount');
const modalConfirmBtn = document.getElementById('modal-confirm');
const modalCancelBtn = document.getElementById('modal-cancel');

// --- Initialization ---

if (currentToken && currentAccount) {
    showDashboard();
}

// --- Auth Switching ---

loginTab.addEventListener('click', () => {
    loginTab.classList.add('active');
    registerTab.classList.remove('active');
    loginForm.classList.add('active');
    registerForm.classList.remove('active');
});

registerTab.addEventListener('click', () => {
    registerTab.classList.add('active');
    loginTab.classList.remove('active');
    registerForm.classList.add('active');
    loginForm.classList.remove('active');
});

// --- API Calls ---

async function apiRequest(endpoint, method = 'GET', body = null, isAuth = true) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json'
        }
    };

    if (isAuth && currentToken) {
        options.headers['Authorization'] = `Bearer ${currentToken}`;
    }

    if (body) {
        options.body = JSON.stringify(body);
    }

    // Special case for params instead of JSON for some Spring Boot endpoints if needed
    // However, we updated the backend to use RequestParams for deposit/withdraw.
    // Handling those explicitly below.

    try {
        const response = await fetch(`${API_URL}${endpoint}`, options);
        if (!response.ok) {
            const errData = await response.json().catch(() => ({}));
            throw new Error(errData.message || 'Operation failed');
        }
        return await response.json();
    } catch (err) {
        showNotification(err.message, true);
        throw err;
    }
}

// --- Auth Operations ---

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const account = document.getElementById('login-account').value;
    const pin = document.getElementById('login-pin').value;

    try {
        const res = await fetch(`${API_URL}/auth/login?accountNumber=${account}&pin=${pin}`, {
            method: 'POST'
        });
        const data = await res.json();
        
        if (res.ok) {
            saveSession(data.token, account);
            showDashboard();
            showNotification('Welcome back!');
        } else {
            showNotification(data.message || 'Login failed', true);
        }
    } catch (err) {
        showNotification('Server connection failed', true);
    }
});

registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const account = document.getElementById('reg-account').value;
    const pin = document.getElementById('reg-pin').value;
    const balance = document.getElementById('reg-balance').value;

    try {
        await apiRequest('/atm/create', 'POST', {
            accountNumber: account,
            pin: pin,
            balance: parseFloat(balance)
        }, false);
        showNotification('Account created! Please login.');
        loginTab.click();
    } catch (err) {
        // Error handled in apiRequest
    }
});

function saveSession(token, account) {
    currentToken = token;
    currentAccount = account;
    localStorage.setItem('jwtToken', token);
    localStorage.setItem('accountNumber', account);
}

function logout() {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('accountNumber');
    currentToken = null;
    currentAccount = null;
    dashboard.classList.add('hidden');
    authOverlay.classList.remove('hidden');
    authOverlay.style.opacity = '1';
}

logoutBtn.addEventListener('click', logout);

// --- Dashboard Logic ---

async function showDashboard() {
    authOverlay.style.opacity = '0';
    setTimeout(() => {
        authOverlay.classList.add('hidden');
        dashboard.classList.remove('hidden');
    }, 400);

    accountDisplay.textContent = `Account: ${currentAccount}`;
    refreshData();
}

async function refreshData() {
    try {
        const balance = await apiRequest(`/atm/balance/${currentAccount}`);
        balanceDisplay.textContent = `$${balance.toLocaleString(undefined, { minimumFractionDigits: 2 })}`;
        
        const history = await apiRequest(`/atm/history/${currentAccount}`);
        updateHistoryUI(history);
    } catch (err) {
        if (err.message.includes('expired') || err.message.includes('JWT')) {
            logout();
        }
    }
}

function updateHistoryUI(history) {
    historyBody.innerHTML = '';
    history.forEach(tx => {
        const row = document.createElement('tr');
        const date = new Date(tx.timestamp).toLocaleString();
        row.innerHTML = `
            <td>${date}</td>
            <td class="type-${tx.type.toLowerCase()}">${tx.type}</td>
            <td>$${tx.amount.toLocaleString(undefined, { minimumFractionDigits: 2 })}</td>
        `;
        historyBody.appendChild(row);
    });
}

// --- Modal Operations ---

document.getElementById('deposit-trigger').addEventListener('click', () => openModal('deposit'));
document.getElementById('withdraw-trigger').addEventListener('click', () => openModal('withdraw'));

function openModal(op) {
    currentOperation = op;
    modalTitle.textContent = op === 'deposit' ? 'Deposit Funds' : 'Withdraw Funds';
    modalAmountInput.value = '';
    modal.classList.add('active');
}

modalCancelBtn.addEventListener('click', () => modal.classList.remove('active'));

modalConfirmBtn.addEventListener('click', async () => {
    const amount = modalAmountInput.value;
    if (!amount || amount <= 0) return showNotification('Enter a valid amount', true);

    try {
        // Use query params as per backend implementation
        const endpoint = `/atm/${currentOperation}?accountNumber=${currentAccount}&amount=${amount}`;
        await apiRequest(endpoint, 'POST');
        
        modal.classList.remove('active');
        showNotification(`${currentOperation.charAt(0).toUpperCase() + currentOperation.slice(1)} successful!`);
        refreshData();
    } catch (err) {
        // Error handled in apiRequest
    }
});

// --- Utilities ---

function showNotification(msg, isError = false) {
    notification.textContent = msg;
    notification.className = 'notification' + (isError ? ' error' : '');
    notification.classList.add('active');
    setTimeout(() => notification.classList.remove('active'), 3000);
}
