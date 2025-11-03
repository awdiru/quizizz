document.addEventListener('DOMContentLoaded', function() {
    const loadingElement = document.getElementById('loading');
    const successElement = document.getElementById('success');
    const errorElement = document.getElementById('error');
    const successMessageElement = document.getElementById('successMessage');
    const errorMessageElement = document.getElementById('errorMessage');
    const loginButton = document.getElementById('loginButton');
    const retryButton = document.getElementById('retryButton');

    // Получаем параметры из URL
    const urlParams = new URLSearchParams(window.location.search);
    const login = urlParams.get('login');
    const token = urlParams.get('token');

    // Проверяем наличие необходимых параметров
    if (!login || !token) {
        showError('Отсутствуют необходимые параметры для подтверждения регистрации.');
        return;
    }

    // Отправляем запрос на подтверждение
    confirmRegistration(login, token);

    // Обработчик кнопки "Перейти к входу"
    loginButton.addEventListener('click', function() {
        window.location.href = '../index.html';
    });

    // Обработчик кнопки "Попробовать снова"
    retryButton.addEventListener('click', function() {
        if (login && token) {
            showLoading();
            confirmRegistration(login, token);
        } else {
            showError('Не удалось получить параметры для повторной попытки.');
        }
    });

    async function confirmRegistration(login, token) {
        try {
            const response = await fetch(`http://localhost:8080/confirmed?login=${encodeURIComponent(login)}&token=${encodeURIComponent(token)}`, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                }
            });

            const data = await response.json();

            if (response.ok) {
                showSuccess(data.message || `Регистрация пользователя ${login} успешно подтверждена.`);
            } else {
                showError(data.message || 'Произошла ошибка при подтверждении регистрации.');
            }
        } catch (error) {
            console.error('Ошибка:', error);
            showError('Ошибка сети: ' + error.message);
        }
    }

    function showLoading() {
        loadingElement.style.display = 'block';
        successElement.style.display = 'none';
        errorElement.style.display = 'none';
    }

    function showSuccess(message) {
        loadingElement.style.display = 'none';
        successElement.style.display = 'block';
        errorElement.style.display = 'none';
        successMessageElement.textContent = message;
    }

    function showError(message) {
        loadingElement.style.display = 'none';
        successElement.style.display = 'none';
        errorElement.style.display = 'block';
        errorMessageElement.textContent = message;
    }
});
