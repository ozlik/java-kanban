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
    InMemoryTaskManager taskManager;
    Task task;
    Epic epic;
    SubTask subtask;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @DisplayName("добавлять задачу в историю")
    @Test
    void shouldAddTaskToHistory() {
        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW));
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        assertEquals(historyManager.getAll().size(), 3, "в историю добавлены не все задачи");
        assertEqualsTask(historyManager.getAll().get(0), task, "задачи не совпадают");
        assertEqualsTask(historyManager.getAll().get(2), subtask, "подзадачи не совпадают");
        assertEqualsTask(historyManager.getAll().get(1), epic, "эпики не совпадают");
    }

    @DisplayName("добавлять задачу в историю")
    @Test
    void shouldAddTaskToHistoryByGetTaskById() {
        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW));
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));
        taskManager.getTaskByID(task.getId());
        assertEquals(historyManager.getAll().size(), 1, "в историю не добавлена задача");

        taskManager.getSubtaskById(subtask.getId());
        assertEquals(historyManager.getAll().size(), 2, "в историю не добавлена подзадача");

        taskManager.getEpicByID(epic.getId());
        assertEquals(historyManager.getAll().size(), 3, "в историю не добавлен эпик");
    }

    @DisplayName("устанавливать длину списка истории задач равной 10")
    @Test
    void shouldRemoveFirstTaskFromHistoryIfHistorySize10AndAddNewTaskToTheEnd() {
        task = taskManager.createTask(new Task("Тестовая задача 0, заголовок", "Описание тестовой задачи", TaskStatus.NEW));
        Task task1 = taskManager.createTask(new Task("Тестовая задача 1, заголовок", "Описание тестовой задачи", TaskStatus.NEW));
        Task task2 = taskManager.createTask(new Task("Тестовая задача 2, заголовок", "Описание тестовой задачи", TaskStatus.NEW));

        taskManager.getTaskByID(task1.getId());
        taskManager.getTaskByID(task.getId());
        taskManager.getTaskByID(task.getId());
        taskManager.getTaskByID(task.getId());
        taskManager.getTaskByID(task.getId());
        taskManager.getTaskByID(task.getId());
        taskManager.getTaskByID(task.getId());
        taskManager.getTaskByID(task.getId());
        taskManager.getTaskByID(task.getId());
        taskManager.getTaskByID(task.getId());

        assertEquals(historyManager.getAll().size(), 10, "в историю не добавлена задача");
        assertEqualsTask(historyManager.getAll().getFirst(), task1, "задачи не совпадают");

        taskManager.getTaskByID(task2.getId());

        assertEquals(historyManager.getAll().size(), 10, "в истории не 10 задач");
        assertEqualsTask(historyManager.getAll().get(0), task, "задачи добавляются не паровозиком (начало)");
        assertEqualsTask(historyManager.getAll().get(9), task2, "задачи добавляются не паровозиком (конец)");

    }

    private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getId(), actual.getId(), message + " , id");
        assertEquals(expected.getTitle(), actual.getTitle(), message + " , title");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " , description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + " , status");
        assertEquals(expected.getType(), actual.getType(), message + " , type");
    }
}
