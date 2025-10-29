const API_BASE = 'http://localhost:8080/directory/get';
const API_CREATE = 'http://localhost:8080/directory/create';

let currentPath = '.'; // Будем хранить текущий путь

// Загрузка директории при старте
document.addEventListener('DOMContentLoaded', () => {
    loadDirectory('.');

    // Обработчики для создания директории
    document.getElementById('createDirBtn').addEventListener('click', showCreateDirInput);
    document.getElementById('createDirInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            createDirectory(this.value);
            this.value = '';
            this.style.display = 'none';
        }
    });

    // Скрываем поле ввода при потере фокуса
    document.getElementById('createDirInput').addEventListener('blur', function() {
        this.style.display = 'none';
    });

    // Обработчик для кнопки "Создать тест"
    document.getElementById('createTestBtn').addEventListener('click', function() {
        // Передаем текущий путь как параметр URL
        window.location.href = `../create_test/create_test.html?path=${encodeURIComponent(currentPath)}`;
    });
});

async function loadDirectory(path) {
    try {
        currentPath = path; // Сохраняем текущий путь

        const encodedPath = encodeURIComponent(path);
        const response = await fetch(`${API_BASE}?path=${encodedPath}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        updateBreadcrumbs(data.path);
        renderContent(data.children, data.path);
    } catch (error) {
        console.error('Ошибка загрузки:', error);
        document.getElementById('content').innerHTML = '<div class="error">Ошибка загрузки: ' + error.message + '</div>';
    }
}

function updateBreadcrumbs(currentPath) {
    const breadcrumb = document.getElementById('breadcrumb');

    // Разбиваем путь на части
    const parts = currentPath.split('/').filter(p => p !== '' && p !== '.');

    let breadcrumbHTML = '<a onclick="loadDirectory(\'.\')">Корневая директория</a>';
    let accumulatedPath = '.';

    // Строим хлебные крошки
    parts.forEach(part => {
        // Формируем путь для каждой части
        accumulatedPath = accumulatedPath === '.' ? `./${part}` : `${accumulatedPath}/${part}`;
        breadcrumbHTML += ` / <a onclick="loadDirectory('${accumulatedPath}')">${part}</a>`;
    });

    breadcrumb.innerHTML = breadcrumbHTML;
}

function renderContent(children, currentPath) {
    const content = document.getElementById('content');

    // Всегда начинаем с пустого содержимого
    let contentHTML = '';

    // Добавляем родительскую директорию "..." если мы не в корне
    if (currentPath !== '.') {
        const parentPath = getParentPath(currentPath);
        contentHTML += `<div class="parent-directory" onclick="loadDirectory('${parentPath}')">📁 ...</div>`;
    }

    // Проверяем, есть ли элементы в директории
    if (children.length === 0) {
        // Если нет элементов, показываем сообщение о пустой директории
        contentHTML += '<div class="empty-dir">Пустая директория</div>';
    } else {
        // Добавляем остальные элементы
        contentHTML += children.map(item => {
            // Формируем полный путь к элементу
            const itemPath = currentPath === '.' ? `./${item.name}` : `${currentPath}/${item.name}`;

            if (item.directory) {
                // Для директорий - кликабельный элемент
                return `<div class="directory" onclick="loadDirectory('${itemPath}')">📁 ${item.name}</div>`;
            } else {
                // Для файлов - просто текст
                return `<div class="file">📄 ${item.name}</div>`;
            }
        }).join('');
    }

    content.innerHTML = contentHTML;
}

function getParentPath(currentPath) {
    // Если текущий путь - корневой, возвращаем его же
    if (currentPath === '.') {
        return '.';
    }

    // Разбиваем путь на части
    const parts = currentPath.split('/').filter(p => p !== '' && p !== '.');

    // Убираем последний элемент
    parts.pop();

    // Если после удаления последнего элемента массив пуст, возвращаем корневую директорию
    if (parts.length === 0) {
        return '.';
    }

    // Собираем путь обратно
    return './' + parts.join('/');
}

function showCreateDirInput() {
    const input = document.getElementById('createDirInput');
    input.style.display = 'inline-block';
    input.focus();
}

async function createDirectory(dirName) {
    if (!dirName.trim()) {
        return; // Не создаем директорию с пустым именем
    }

    try {
        // Формируем полный путь к новой директории
        const newDirPath = currentPath === '.' ? `./${dirName}` : `${currentPath}/${dirName}`;

        const response = await fetch(API_CREATE, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                path: newDirPath
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // Обновляем текущую директорию после успешного создания
        loadDirectory(currentPath);

    } catch (error) {
        console.error('Ошибка создания директории:', error);
        alert('Ошибка при создании директории: ' + error.message);
    }
}