// Функция для проверки авторизации
function isAuthenticated() {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');
    return !!(token && username);
}

// Функция для получения заголовков авторизации
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    const username = localStorage.getItem('username');

    if (!token || !username) {
        return null;
    }

    return {
        'X-Token': token,
        'X-Username': username
    };
}

// Функция для выполнения запросов с авторизацией
async function fetchWithAuth(url, options = {}) {
    // Получаем заголовки авторизации
    const authHeaders = getAuthHeaders();

    if (!authHeaders) {
        // Если нет авторизации, перенаправляем на страницу входа
        redirectToLogin();
        return Promise.reject(new Error('Требуется авторизация'));
    }

    // Добавляем заголовки авторизации к существующим заголовкам
    const headers = {
        ...options.headers,
        ...authHeaders
    };

    // Выполняем запрос
    const response = await fetch(url, {
        ...options,
        headers: headers
    });

    // Обрабатываем ответ
    if (response.status === 401) {
        // Если сервер вернул 401, очищаем данные и перенаправляем на вход
        clearAuth();
        redirectToLogin();
        return Promise.reject(new Error('Неверный токен авторизации'));
    }

    return response;
}

// Функция для очистки данных авторизации
function clearAuth() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
}

// Функция для перенаправления на страницу входа
function redirectToLogin() {
    // Не перенаправляем, если уже на странице входа
    if (window.location.pathname.endsWith('index.html') ||
        window.location.pathname === '/' ||
        window.location.pathname.endsWith('/')) {
        return;
    }

    window.location.href = '../index.html';
}

// Функция для проверки авторизации при загрузке страницы
function checkAuthOnLoad() {
    if (!isAuthenticated()) {
        redirectToLogin();
        return false;
    }
    return true;
}