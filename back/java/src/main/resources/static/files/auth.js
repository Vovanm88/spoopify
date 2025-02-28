
async function hashPassword(password) {
    return ''+password;
    const encoder = new TextEncoder();
    const data = encoder.encode(password);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashHex = hashArray.map(byte => byte.toString(16).padStart(2, '0')).join('');
    return hashHex;
}

async function _registerUser(username, login, password) {
    const response = await fetch('/v1/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ 
            username, 
            login, 
            password // Хеширование пароля перед отправкой
        })
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
}

async function _loginUser(email, password) {
    //const password = await hashPassword(_password);
    const loginData = JSON.stringify({ email, password });

    const response = await fetch('/v1/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            //'Content-Length': loginData.length.toString() // Установка длины контента
        },
        body: loginData
    });

    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
}

async function register() {
    const username = document.getElementById('register-username').value;
    const login = document.getElementById('register-login').value;
    const password = document.getElementById('register-password').value;
    try {
        const result = await _registerUser(username, login, password);
        // alert('Registration successful: ' + JSON.stringify(result));
        displayMessage('Registration successful!'); // Отображаем сообщение об успехе
    } catch (error) {
        // alert('Registration failed: ' + error);
        displayMessage('Registration failed: ' + error, 'error'); // Отображаем сообщение об ошибке
    }
}

async function login() {
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    try {
        const result = await _loginUser(email, password);
        localStorage.setItem('token', result.token); // Store the token
        // alert('Login successful: ' + JSON.stringify(result));
        displayMessage('Login successful!'); // Отображаем сообщение об успехе
        updateAuthSection(); // Обновляем секцию аутентификации после входа
        updateSongSectionVisibility();

    } catch (error) {
        // alert('Login failed: ' + error);
        displayMessage('Login failed: ' + error, 'error'); // Отображаем сообщение об ошибке
    }
}


function displayMessage(message, type) {
    const messageDiv = document.createElement('div');
    messageDiv.textContent = message;
    messageDiv.className = 'message ' + type; // Добавляем класс для стилизации
    const authSection = document.getElementById('auth-section');
    authSection.appendChild(messageDiv);

    // Автоматически удаляем сообщение через 3 секунды
    setTimeout(() => {
        messageDiv.remove();
    }, 3000);
}

function logout() {
    localStorage.removeItem('token'); // Удаляем токен
    updateAuthSection(); // Обновляем секцию аутентификации после выхода
    displayMessage('Logged out successfully!', 'success'); // Отображаем сообщение об успехе
}

function updateAuthSection() {
    const authSection = document.getElementById('auth-section');
    const registerForm = document.getElementById('register-form');
    const loginForm = document.getElementById('login-form');
    const logoutButton = document.getElementById('logout-button');

    if (localStorage.getItem('token')) {
        // Если пользователь залогинен
        registerForm.style.display = 'none'; // Скрываем форму регистрации
        loginForm.style.display = 'none'; // Скрываем форму входа
        logoutButton.style.display = 'block'; // Показываем кнопку выхода
    } else {
        // Если пользователь не залогинен
        registerForm.style.display = 'block'; // Показываем форму регистрации
        loginForm.style.display = 'block'; // Показываем форму входа
        logoutButton.style.display = 'none'; // Скрываем кнопку выхода
    }
}
function updateSongSectionVisibility() {
    const songSection = document.getElementById('song-section');
    songSection.style.display = localStorage.getItem('token') ? 'block' : 'none';
}
window.onload = () => {
    updateAuthSection();
    updateSongSectionVisibility();
};