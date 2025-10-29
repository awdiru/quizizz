document.addEventListener('DOMContentLoaded', function() {
    // Проверяем авторизацию при загрузке страницы
    if (!checkAuthOnLoad()) {
        return;
    }

    const questionsContainer = document.getElementById('questionsContainer');
    const addQuestionBtn = document.getElementById('addQuestionBtn');
    const createTestBtn = document.getElementById('createTestBtn');
    const testNameInput = document.getElementById('testName');

    let questionCount = 0;
    let answerCount = 0;

    // Добавление первого вопроса при загрузке
    addQuestion();

    // Обработчик кнопки "Добавить вопрос"
    addQuestionBtn.addEventListener('click', addQuestion);

    // Обработчик кнопки "Создать тест"
    createTestBtn.addEventListener('click', createTest);

    function addQuestion() {
        questionCount++;
        const questionId = `question-${questionCount}`;

        const questionElement = document.createElement('div');
        questionElement.className = 'question';
        questionElement.id = questionId;

        questionElement.innerHTML = `
            <div class="question-header">
                <input type="text" class="question-input" placeholder="Введите вопрос ${questionCount}">
                <button class="add-answer-btn">Добавить ответ</button>
            </div>
            <div class="answers-container" id="answers-${questionCount}">
                <!-- Ответы будут добавляться здесь -->
            </div>
        `;

        questionsContainer.appendChild(questionElement);

        // Добавляем обработчик для кнопки добавления ответа
        const addAnswerBtn = questionElement.querySelector('.add-answer-btn');
        addAnswerBtn.addEventListener('click', function() {
            addAnswer(questionCount);
        });

        // Добавляем первый ответ к вопросу
        addAnswer(questionCount);
    }

    function addAnswer(questionNumber) {
        answerCount++;
        const answerId = `answer-${answerCount}`;
        const answersContainer = document.getElementById(`answers-${questionNumber}`);

        const answerElement = document.createElement('div');
        answerElement.className = 'answer';
        answerElement.id = answerId;

        answerElement.innerHTML = `
            <input type="text" class="answer-input" placeholder="Введите ответ">
            <label class="correct-answer-label">
                <input type="checkbox" class="correct-answer-checkbox">
                Правильный ответ
            </label>
            <button class="remove-answer-btn">×</button>
        `;

        answersContainer.appendChild(answerElement);

        // Обработчик кнопки удаления ответа
        const removeAnswerBtn = answerElement.querySelector('.remove-answer-btn');
        removeAnswerBtn.addEventListener('click', function() {
            answerElement.remove();
        });
    }

    async function createTest() {
        const testName = testNameInput.value.trim();

        if (!testName) {
            alert('Введите название теста');
            testNameInput.focus();
            return;
        }

        const questions = [];
        const questionElements = document.querySelectorAll('.question');

        if (questionElements.length === 0) {
            alert('Добавьте хотя бы один вопрос');
            return;
        }

        let hasError = false;

        questionElements.forEach((questionElement, questionIndex) => {
            const questionInput = questionElement.querySelector('.question-input');
            const questionText = questionInput.value.trim();

            if (!questionText) {
                alert(`Введите текст для вопроса ${questionIndex + 1}`);
                questionInput.focus();
                hasError = true;
                return;
            }

            const answers = [];
            const answerElements = questionElement.querySelectorAll('.answer');

            if (answerElements.length === 0) {
                alert(`Добавьте хотя бы один ответ для вопроса "${questionText}"`);
                hasError = true;
                return;
            }

            answerElements.forEach((answerElement, answerIndex) => {
                const answerInput = answerElement.querySelector('.answer-input');
                const answerText = answerInput.value.trim();
                const isCorrect = answerElement.querySelector('.correct-answer-checkbox').checked;

                if (!answerText) {
                    alert(`Введите текст для ответа ${answerIndex + 1} в вопросе "${questionText}"`);
                    answerInput.focus();
                    hasError = true;
                    return;
                }

                answers.push({
                    answer: answerText,
                    number: answerIndex + 1,
                    right: isCorrect
                });
            });

            if (!hasError) {
                questions.push({
                    question: questionText,
                    answers: answers
                });
            }
        });

        if (hasError) {
            return;
        }

        // Получаем текущий путь из URL параметров
        const urlParams = new URLSearchParams(window.location.search);
        const currentPath = urlParams.get('path') || '.';

        // Формируем полный путь к тесту
        const testPath = currentPath === '.' ? `./${testName}` : `${currentPath}/${testName}`;

        // Отправляем запрос на создание теста
        try {
            const response = await fetchWithAuth('http://localhost:8080/tests/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    path: testPath,
                    questions: questions
                })
            });

            if (!response.ok) {
                throw new Error('Ошибка при создании теста');
            }

            alert('Тест успешно создан!');
            window.location.href = '../directories/directories.html';
        } catch (error) {
            console.error('Ошибка:', error);
            // Не показываем ошибку, если это редирект на авторизацию
            if (!error.message.includes('авторизация') && !error.message.includes('токен')) {
                alert('Ошибка при создании теста: ' + error.message);
            }
        }
    }
});