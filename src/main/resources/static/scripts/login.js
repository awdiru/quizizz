document.addEventListener('DOMContentLoaded', function() {
    const loginButton = document.getElementById('loginButton');
    const passwordInput = document.getElementById('passwordInput');

    loginButton.addEventListener('click', function() {
        const password = passwordInput.value;

        fetch('/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                password: password
            })
        })
            .then(response => {
                if (response.ok) {
                    // При успешном ответе перенаправляем на directories.html
                    window.location.href = '../directories/directories.html';
                } else {
                    throw new Error('Ошибка сети: ' + response.status);
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Ошибка при входе: ' + error.message);
            });
    });
});