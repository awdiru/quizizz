const SERVER_ADDRESS = 'http://localhost:8080'
const API_BASE = SERVER_ADDRESS + '/directory/get';
const API_CREATE = SERVER_ADDRESS + '/directory/create';
const API_RENAME = SERVER_ADDRESS + '/directory/rename';
const API_REMOVE = SERVER_ADDRESS + '/directory/remove';
const API_TEST_RENAME = SERVER_ADDRESS + '/tests/rename';
const API_TEST_REMOVE = SERVER_ADDRESS + '/tests/remove';

let currentPath = '.';
let activeDropdown = null;

// Загрузка директории при старте
document.addEventListener('DOMContentLoaded', () => {
    // Проверяем авторизацию при загрузке страницы
    if (!checkAuthOnLoad()) {
        return;
    }

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
        window.location.href = `../create_test/create_test.html?path=${encodeURIComponent(currentPath)}`;
    });

    // Закрываем выпадающие меню при клике вне их
    document.addEventListener('click', function(e) {
        if (activeDropdown && !e.target.closest('.directory-actions') && !e.target.closest('.file-actions')) {
            activeDropdown.classList.remove('show');
            activeDropdown = null;
        }
    });
});

async function loadDirectory(path) {
    try {
        currentPath = path;

        const encodedPath = encodeURIComponent(path);
        const response = await fetchWithAuth(`${API_BASE}?path=${encodedPath}`);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        updateBreadcrumbs(data.path);
        renderContent(data.children, data.path);
    } catch (error) {
        console.error('Ошибка загрузки:', error);
        if (!error.message.includes('авторизация') && !error.message.includes('токен')) {
            document.getElementById('content').innerHTML = '<div class="error">Ошибка загрузки: ' + error.message + '</div>';
        }
    }
}

function updateBreadcrumbs(currentPath) {
    const breadcrumb = document.getElementById('breadcrumb');

    const parts = currentPath.split('/').filter(p => p !== '' && p !== '.');

    let breadcrumbHTML = '<a onclick="loadDirectory(\'.\')">Корневая директория</a>';
    let accumulatedPath = '.';

    parts.forEach(part => {
        accumulatedPath = accumulatedPath === '.' ? `./${part}` : `${accumulatedPath}/${part}`;
        breadcrumbHTML += ` / <a onclick="loadDirectory('${accumulatedPath}')">${part}</a>`;
    });

    breadcrumb.innerHTML = breadcrumbHTML;
}

function renderContent(children, currentPath) {
    const content = document.getElementById('content');

    let contentHTML = '';

    // Добавляем родительскую директорию "..." если мы не в корне
    if (currentPath !== '.') {
        const parentPath = getParentPath(currentPath);
        contentHTML += `
            <div class="directory-item">
                <div class="directory-name" onclick="loadDirectory('${parentPath}')">📁 ...</div>
            </div>
        `;
    }

    // Проверяем, есть ли элементы в директории
    if (children.length === 0) {
        contentHTML += '<div class="empty-dir">Пустая директория</div>';
    } else {
        // Добавляем остальные элементы
        contentHTML += children.map(item => {
            const itemPath = currentPath === '.' ? `./${item.name}` : `${currentPath}/${item.name}`;

            if (item.directory) {
                return `
                    <div class="directory-item">
                        <div class="directory-name" onclick="loadDirectory('${itemPath}')">📁 ${item.name}</div>
                        <div class="created">${item.created}</div>
                        <div class="directory-actions">
                            <button class="action-btn" onclick="toggleDropdown(this)">⋯</button>
                            <div class="dropdown-menu">
                                <div class="dropdown-item" onclick="startRenameDirectory('${itemPath}', '${item.name}')">Переименовать директорию</div>
                                <div class="dropdown-item delete" onclick="confirmDeleteDirectory('${itemPath}', '${item.name}')">Удалить директорию</div>
                            </div>
                        </div>
                    </div>
                `;
            } else {
                return `
                    <div class="file-item">
                        <div class="file-name">📄 ${item.name}</div>
                        <div class="created">${item.created}</div>
                        <div class="file-actions">
                            <button class="action-btn" onclick="toggleDropdown(this)">⋯</button>
                            <div class="dropdown-menu">
                                <div class="dropdown-item" onclick="startRenameFile('${itemPath}', '${item.name}')">Переименовать файл</div>
                                <div class="dropdown-item delete" onclick="confirmDeleteFile('${itemPath}', '${item.name}')">Удалить файл</div>
                            </div>
                        </div>
                    </div>
                `;
            }
        }).join('');
    }

    content.innerHTML = contentHTML;
}

function getParentPath(currentPath) {
    if (currentPath === '.') {
        return '.';
    }

    const parts = currentPath.split('/').filter(p => p !== '' && p !== '.');
    parts.pop();

    if (parts.length === 0) {
        return '.';
    }

    return './' + parts.join('/');
}

function showCreateDirInput() {
    const input = document.getElementById('createDirInput');
    input.style.display = 'inline-block';
    input.focus();
}

async function createDirectory(dirName) {
    if (!dirName.trim()) {
        return;
    }

    try {
        const newDirPath = currentPath === '.' ? `./${dirName}` : `${currentPath}/${dirName}`;

        const response = await fetchWithAuth(API_CREATE, {
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

        loadDirectory(currentPath);

    } catch (error) {
        console.error('Ошибка создания директории:', error);
        if (!error.message.includes('авторизация') && !error.message.includes('токен')) {
            alert('Ошибка при создании директории: ' + error.message);
        }
    }
}

function toggleDropdown(button) {
    const dropdown = button.nextElementSibling;

    // Закрываем активное выпадающее меню
    if (activeDropdown && activeDropdown !== dropdown) {
        activeDropdown.classList.remove('show');
    }

    // Переключаем текущее меню
    dropdown.classList.toggle('show');
    activeDropdown = dropdown.classList.contains('show') ? dropdown : null;

    // Предотвращаем всплытие события
    event.stopPropagation();
}

// Функции для работы с директориями
function startRenameDirectory(path, currentName) {
    // Закрываем выпадающее меню
    if (activeDropdown) {
        activeDropdown.classList.remove('show');
        activeDropdown = null;
    }

    // Находим элемент директории
    const directoryItems = document.querySelectorAll('.directory-item');
    let targetItem = null;

    directoryItems.forEach(item => {
        const dirName = item.querySelector('.directory-name');
        if (dirName && dirName.textContent.includes(currentName)) {
            targetItem = item;
        }
    });

    if (!targetItem) return;

    const directoryName = targetItem.querySelector('.directory-name');
    const actions = targetItem.querySelector('.directory-actions');

    // Сохраняем оригинальный обработчик
    const originalOnClick = directoryName.onclick;

    // Заменяем на поле ввода
    directoryName.outerHTML = `
        <input type="text" 
               class="rename-input" 
               value="${currentName}" 
               onkeypress="handleRenameDirectoryKeypress(event, '${path}')"
               onblur="cancelRenameDirectory(this, '${currentName}', originalOnClick)">
    `;

    const input = targetItem.querySelector('.rename-input');
    input.select();
    input.focus();

    // Скрываем кнопки действий во время редактирования
    actions.style.display = 'none';

    // Сохраняем ссылки для восстановления
    input._originalName = currentName;
    input._originalOnClick = originalOnClick;
    input._actions = actions;
}

function handleRenameDirectoryKeypress(event, path) {
    if (event.key === 'Enter') {
        const newName = event.target.value.trim();
        if (newName && newName !== event.target._originalName) {
            renameDirectory(path, newName, event.target);
        } else {
            cancelRenameDirectory(event.target, event.target._originalName, event.target._originalOnClick);
        }
    } else if (event.key === 'Escape') {
        cancelRenameDirectory(event.target, event.target._originalName, event.target._originalOnClick);
    }
}

async function renameDirectory(path, newName, inputElement) {
    try {
        const response = await fetchWithAuth(API_RENAME, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                path: path,
                newName: newName
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // Обновляем текущую директорию
        loadDirectory(currentPath);

    } catch (error) {
        console.error('Ошибка переименования директории:', error);
        if (!error.message.includes('авторизация') && !error.message.includes('токен')) {
            alert('Ошибка при переименовании директории: ' + error.message);
            // Восстанавливаем оригинальное имя в случае ошибки
            cancelRenameDirectory(inputElement, inputElement._originalName, inputElement._originalOnClick);
        }
    }
}

function cancelRenameDirectory(inputElement, originalName, originalOnClick) {
    const parent = inputElement.parentElement;

    // Восстанавливаем оригинальный элемент
    inputElement.outerHTML = `
        <div class="directory-name" onclick="(${originalOnClick})()">📁 ${originalName}</div>
    `;

    // Показываем кнопки действий
    if (inputElement._actions) {
        inputElement._actions.style.display = 'block';
    }
}

function confirmDeleteDirectory(path, name) {
    // Закрываем выпадающее меню
    if (activeDropdown) {
        activeDropdown.classList.remove('show');
        activeDropdown = null;
    }

    // Создаем диалог подтверждения
    const dialog = document.createElement('div');
    dialog.className = 'confirmation-dialog';
    dialog.innerHTML = `
        <div class="confirmation-content">
            <p>Вы действительно хотите удалить директорию "${name}"? Все файлы так же будут удалены</p>
            <div class="confirmation-buttons">
                <button class="cancel-btn" onclick="this.closest('.confirmation-dialog').remove()">Отмена</button>
                <button class="confirm-btn" onclick="deleteDirectory('${path}')">Ок</button>
            </div>
        </div>
    `;

    document.body.appendChild(dialog);
}

async function deleteDirectory(path) {
    try {
        // Удаляем диалог подтверждения
        const dialog = document.querySelector('.confirmation-dialog');
        if (dialog) {
            dialog.remove();
        }

        const encodedPath = encodeURIComponent(path);
        const response = await fetchWithAuth(`${API_REMOVE}?path=${encodedPath}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // Обновляем текущую директорию
        loadDirectory(currentPath);

    } catch (error) {
        console.error('Ошибка удаления директории:', error);
        if (!error.message.includes('авторизация') && !error.message.includes('токен')) {
            alert('Ошибка при удалении директории: ' + error.message);
        }
    }
}

// Функции для работы с файлами
function startRenameFile(path, currentName) {
    // Закрываем выпадающее меню
    if (activeDropdown) {
        activeDropdown.classList.remove('show');
        activeDropdown = null;
    }

    // Находим элемент файла
    const fileItems = document.querySelectorAll('.file-item');
    let targetItem = null;

    fileItems.forEach(item => {
        const fileName = item.querySelector('.file-name');
        if (fileName && fileName.textContent.includes(currentName)) {
            targetItem = item;
        }
    });

    if (!targetItem) return;

    const fileName = targetItem.querySelector('.file-name');
    const actions = targetItem.querySelector('.file-actions');

    // Заменяем на поле ввода
    fileName.outerHTML = `
        <input type="text" 
               class="rename-input" 
               value="${currentName}" 
               onkeypress="handleRenameFileKeypress(event, '${path}')"
               onblur="cancelRenameFile(this, '${currentName}')">
    `;

    const input = targetItem.querySelector('.rename-input');
    input.select();
    input.focus();

    // Скрываем кнопки действий во время редактирования
    actions.style.display = 'none';

    // Сохраняем ссылки для восстановления
    input._originalName = currentName;
    input._actions = actions;
}

function handleRenameFileKeypress(event, path) {
    if (event.key === 'Enter') {
        const newName = event.target.value.trim();
        if (newName && newName !== event.target._originalName) {
            renameFile(path, newName, event.target);
        } else {
            cancelRenameFile(event.target, event.target._originalName);
        }
    } else if (event.key === 'Escape') {
        cancelRenameFile(event.target, event.target._originalName);
    }
}

async function renameFile(path, newName, inputElement) {
    try {
        const response = await fetchWithAuth(API_TEST_RENAME, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                path: path,
                newName: newName
            })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // Обновляем текущую директорию
        loadDirectory(currentPath);

    } catch (error) {
        console.error('Ошибка переименования файла:', error);
        if (!error.message.includes('авторизация') && !error.message.includes('токен')) {
            alert('Ошибка при переименовании файла: ' + error.message);
            // Восстанавливаем оригинальное имя в случае ошибки
            cancelRenameFile(inputElement, inputElement._originalName);
        }
    }
}

function cancelRenameFile(inputElement, originalName) {
    const parent = inputElement.parentElement;

    // Восстанавливаем оригинальный элемент
    inputElement.outerHTML = `
        <div class="file-name">📄 ${originalName}</div>
    `;

    // Показываем кнопки действий
    if (inputElement._actions) {
        inputElement._actions.style.display = 'block';
    }
}

function confirmDeleteFile(path, name) {
    // Закрываем выпадающее меню
    if (activeDropdown) {
        activeDropdown.classList.remove('show');
        activeDropdown = null;
    }

    // Создаем диалог подтверждения
    const dialog = document.createElement('div');
    dialog.className = 'confirmation-dialog';
    dialog.innerHTML = `
        <div class="confirmation-content">
            <p>Вы действительно хотите удалить файл "${name}"?</p>
            <div class="confirmation-buttons">
                <button class="cancel-btn" onclick="this.closest('.confirmation-dialog').remove()">Отмена</button>
                <button class="confirm-btn" onclick="deleteFile('${path}')">Ок</button>
            </div>
        </div>
    `;

    document.body.appendChild(dialog);
}

async function deleteFile(path) {
    try {
        // Удаляем диалог подтверждения
        const dialog = document.querySelector('.confirmation-dialog');
        if (dialog) {
            dialog.remove();
        }

        const encodedPath = encodeURIComponent(path);
        const response = await fetchWithAuth(`${API_TEST_REMOVE}?path=${encodedPath}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // Обновляем текущую директорию
        loadDirectory(currentPath);

    } catch (error) {
        console.error('Ошибка удаления файла:', error);
        if (!error.message.includes('авторизация') && !error.message.includes('токен')) {
            alert('Ошибка при удалении файла: ' + error.message);
        }
    }
}