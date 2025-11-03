document.addEventListener('DOMContentLoaded', function() {
    const loginButton = document.getElementById('loginButton');
    const registerButton = document.getElementById('registerButton');
    const nameInput = document.getElementById('nameInput');
    const passwordInput = document.getElementById('passwordInput');

    loginButton.addEventListener('click', function() {
        const name = nameInput.value.trim();
        const password = passwordInput.value;

        // Базовые проверки заполнения полей
        if (!name) {
            alert('Введите имя');
            nameInput.focus();
            return;
        }

        if (!password) {
            alert('Введите пароль');
            passwordInput.focus();
            return;
        }

        // Выполняем запрос на вход
        fetch('http://localhost:8080/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                login: name,
                password: password
            })
        })
            .then(response => {
                if (response.status === 401) {
                    throw new Error('Неверное имя или пароль');
                }
                if (!response.ok) {
                    throw new Error('Ошибка сервера: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                if (data.status === 200 && data.token) {
                    // Сохраняем token и имя пользователя в localStorage
                    localStorage.setItem('token', data.token);
                    localStorage.setItem('username', name);
                    // При успешном ответе перенаправляем на directories.html
                    window.location.href = 'directories/directories.html';
                } else {
                    throw new Error(data.message || 'Ошибка входа');
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Ошибка при входе: ' + error.message);
                // Очищаем поле пароля для повторного ввода
                passwordInput.value = '';
                passwordInput.focus();
            });
    });

    // Обработчик кнопки регистрации
    registerButton.addEventListener('click', function () {
        window.location.href = '../register/register.html';
    });

    // Добавляем возможность нажимать Enter для входа
    nameInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            passwordInput.focus();
        }
    });

    passwordInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            loginButton.click();
        }
    });

    // Устанавливаем фокус на поле имени при загрузке
    nameInput.focus();
});
