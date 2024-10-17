package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryHistoryManager должен ")
public class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    Task task;
    Epic epic;
    SubTask subtask;
    private int id = 0;

    private int generatedId() {
        return ++id;
    }


    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
    }

    @DisplayName("добавлять задачу в историю в конец списка и удалять существующее упоминание")
    @Test
    void shouldAddTaskLastToHistory() {
        task = new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW);
        epic = new Epic("Тестовая задача, заголовок", "Описание тестовой задачи");
        task.setId(generatedId());
        epic.setId(generatedId());
        subtask = new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId());
        subtask.setId(generatedId());

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        assertEquals(historyManager.getAll().size(), 3, "в историю добавлены не все задачи");
        assertEqualsTask(historyManager.getAll().get(0), task, "задачи не совпадают");
        assertEqualsTask(historyManager.getAll().get(2), subtask, "подзадачи не совпадают");
        assertEqualsTask(historyManager.getAll().get(1), epic, "эпики не совпадают");

        historyManager.add(task);

        assertEquals(historyManager.getAll().size(), 3, "в историю добавлены не все задачи");
        assertEqualsTask(historyManager.getAll().get(2), task, "задачи не совпадают");
        assertEqualsTask(historyManager.getAll().get(1), subtask, "подзадачи не совпадают");
        assertEqualsTask(historyManager.getAll().get(0), epic, "эпики не совпадают");

    }

    @DisplayName("Удалять задачу из истории")
    @Test
    void shouldRemoveTaskFromHistory() {
        task = new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW);
        epic = new Epic("Тестовая задача, заголовок", "Описание тестовой задачи");
        task.setId(generatedId());
        epic.setId(generatedId());
        subtask = new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId());
        subtask.setId(generatedId());

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);

        historyManager.remove(epic.getId());

        historyManager.add(task);

        assertEquals(historyManager.getAll().size(), 2, "в историю добавлены не все задачи");
        assertEqualsTask(historyManager.getAll().get(1), task, "задачи не совпадают");
        assertEqualsTask(historyManager.getAll().get(0), subtask, "подзадачи не совпадают");

    }

    @DisplayName("Удалять задачу из пустого списка")
    @Test
    void shouldRemoveTaskFromEmptyHistory() {
        task = new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW);
        epic = new Epic("Тестовая задача, заголовок", "Описание тестовой задачи");
        task.setId(generatedId());
        epic.setId(generatedId());
        subtask = new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId());
        subtask.setId(generatedId());

        historyManager.remove(epic.getId());
        boolean i = historyManager.getAll().isEmpty();

        assertEquals(true, i);
    }

    @DisplayName("Добавлять задачу в историю в конец списка и удалять существующее упоминание в пустом списке")
    @Test
    void shouldAddTaskToEmptyHistory() {
        task = new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW);
        epic = new Epic("Тестовая задача, заголовок", "Описание тестовой задачи");
        task.setId(generatedId());
        epic.setId(generatedId());
        subtask = new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId());
        subtask.setId(generatedId());

        historyManager.add(task);

        assertEquals(historyManager.getAll().size(), 1, "в историю добавлены не все задачи");
        assertEqualsTask(historyManager.getAll().getFirst(), task, "задачи не совпадают");

        historyManager.add(task);

        assertEquals(historyManager.getAll().size(), 1, "в историю добавлены не все задачи");
        assertEqualsTask(historyManager.getAll().getFirst(), task, "задачи не совпадают");
    }

    private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getTitle(), actual.getTitle(), message + " , title");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " , description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + " , status");
        assertEquals(expected.getType(), actual.getType(), message + " , type");
    }
}
