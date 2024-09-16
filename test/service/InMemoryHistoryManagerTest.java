package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("InMemoryHistoryManager должен ")
public class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    Task task;
    Epic epic;
    SubTask subtask;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
            }

    @DisplayName("добавлять задачу в историю")
    @Test
    void shouldAddTaskToHistory() {
        task = new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW);
        epic = new Epic("Тестовая задача, заголовок", "Описание тестовой задачи");
        subtask = new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 1);
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(historyManager.getAll().size(), 10, "в историю добавлены не все задачи");
        assertEqualsTask(historyManager.getAll().get(0), task, "задачи не совпадают");
        assertEqualsTask(historyManager.getAll().get(2), subtask, "подзадачи не совпадают");
        assertEqualsTask(historyManager.getAll().get(1), epic, "эпики не совпадают");
        historyManager.add(subtask);
        assertEquals(historyManager.getAll().size(), 10, "в историю добавлены не все задачи");
        assertEqualsTask(historyManager.getAll().get(9), subtask, "подзадачи не совпадают");
        assertEqualsTask(historyManager.getAll().get(1), subtask, "подзадачи не совпадают");
        assertEqualsTask(historyManager.getAll().get(0), epic, "эпики не совпадают");
    }

      private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getTitle(), actual.getTitle(), message + " , title");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " , description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + " , status");
        assertEquals(expected.getType(), actual.getType(), message + " , type");
    }
}
