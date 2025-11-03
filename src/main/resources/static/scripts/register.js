document.addEventListener('DOMContentLoaded', function() {
    const registerButton = document.getElementById('registerButton');
    const backToLoginButton = document.getElementById('backToLoginButton');
    const loginInput = document.getElementById('loginInput');
    const passwordInput = document.getElementById('passwordInput');
    const confirmPasswordInput = document.getElementById('confirmPasswordInput');
    const emailInput = document.getElementById('emailInput');

    registerButton.addEventListener('click', function() {
        const login = loginInput.value.trim();
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;
        const email = emailInput.value.trim();

        // Проверки заполнения полей
        if (!login) {
            alert('Введите логин');
            loginInput.focus();
            return;
        }

        if (!password) {
            alert('Введите пароль');
            passwordInput.focus();
            return;
        }

        if (!confirmPassword) {
            alert('Повторите пароль');
            confirmPasswordInput.focus();
            return;
        }

        if (password !== confirmPassword) {
            alert('Пароли не совпадают');
            passwordInput.value = '';
            confirmPasswordInput.value = '';
            passwordInput.focus();
            return;
        }

        if (!email) {
            alert('Введите email');
            emailInput.focus();
            return;
        }

        // Простая валидация email
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            alert('Введите корректный email');
            emailInput.focus();
            return;
        }

        // Выполняем запрос на регистрацию
        fetch('http://localhost:8080/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                login: login,
                password: password,
                email: email
            })
        })
            .then(response => {
                if (response.status === 200) {
                    return response.text(); // или response.json(), если сервер возвращает JSON
                } else {
                    throw new Error('Ошибка регистрации: ' + response.status);
                }
            })
            .then(data => {
                alert('Регистрация прошла успешно, ожидайте подтверждения работодателем');
                // Перенаправляем на страницу входа
                window.location.href = '../index.html';
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Ошибка при регистрации: ' + error.message);
            });
    });

    // Обработчик кнопки "Назад к входу"
    backToLoginButton.addEventListener('click', function() {
        window.location.href = '../index.html';
    });

    // Добавляем возможность нажимать Enter для перехода между полями и регистрации
    loginInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            passwordInput.focus();
        }
    });

    passwordInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            confirmPasswordInput.focus();
        }
    });

    confirmPasswordInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            emailInput.focus();
        }
    });

    emailInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            registerButton.click();
        }
    });

    // Устанавливаем фокус на поле логина при загрузке
    loginInput.focus();
});
