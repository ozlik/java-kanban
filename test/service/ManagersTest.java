package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Managers должен")
public abstract class ManagersTest<T extends TaskManager> {
    protected T taskManager;
    Task task;
    Epic epic;
    SubTask subtask;

    abstract T createManager();

    @BeforeEach
    void beforeEach() {
        taskManager = createManager();
    }

    @Test
    @DisplayName("корректно передавать в мапу и из мапы созданную задачу")
    void shouldCreateTaskGetAndPutToMap() {
        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 12, 31, 16, 10)));
        List<Task> tasks = taskManager.getTasks();

        assertEqualsTask(task, tasks.getFirst(), "задачи не совпадают");
        assertEquals(1, tasks.size(), "в маше другое количество задач или она пустая");
    }

    @Test
    @DisplayName("корректно передавать в мапу и из мапы созданный эпик")
    void shouldCreateEpicGetAndPutToMap() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        List<Epic> epics = taskManager.getEpics();

        assertEqualsEpic(epic, epics.getFirst(), "эпики не совпадают");
        assertEquals(1, epics.size(), "в мапе другое количество задач или она пустая");
    }

    @Test
    @DisplayName("передавать в мапу и из мапы созданную подзадачу")
    void shouldCreateSubtaskGetAndPutToMap() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 10, 31, 10, 10), epic.getId()));
        List<SubTask> subtasks = taskManager.getSubtasks();

        assertEqualsSubtask(subtask, subtasks.getFirst(), "подзадачи на совпадают");
        assertEquals(1, subtasks.size(), "в маше другое количество задач или она пустая");
    }

    @Test
    @DisplayName("добавлять подзадачу в эпик при создании подзадачи")
    void shouldAddCreatedSubtaskToEpic() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 10, 31, 12, 10), epic.getId()));
        List<Integer> epicSubtasks = epic.getSubTasks();
        Integer id = subtask.getId();

        assertEquals(id, epicSubtasks.getFirst(), "id подзадачи не совпадает");
        assertEquals(1, epicSubtasks.size(), "в эпике другое количество подзадач");
    }

    @Test
    @DisplayName("не создавать подзадачу при попытке создать подзадачу с несуществующим эпиком")
    void shouldNotCreateSubTaskWithWrongEpicId() {
        subtask = new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 10, 31, 11, 10), 80);

        assertEquals(0, taskManager.getSubtasks().size(), "подзадача с несуществующим эпиком добавилась в мапу");
    }

    @Test
    @DisplayName("передавать полный список задач")
    void shouldGetTasks() {
        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 3, 12, 10)));
        Task task1 = taskManager.createTask(new Task("Тестовая задача 2, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 3, 15, 10)));

        ArrayList<Task> tasks = taskManager.getTasks();

        Task taskResult = tasks.getFirst();
        Task taskResult1 = tasks.getLast();
        assertEqualsTask(task, taskResult, "задачи не совпадают");
        assertEqualsTask(task1, taskResult1, "Вторые задачи не совпадают");
        assertEquals(2, tasks.size(), "в мапе не две задачи");
    }

    @Test
    @DisplayName("передавать полный список эпиков")
    void shouldGetEpics() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epic1 = taskManager.createEpic(new Epic("Тестовая задача 2, заголовок", "Описание тестовой задачи"));

        ArrayList<Epic> epics = taskManager.getEpics();

        Epic resultEpic = epics.getFirst();
        Epic resultEpic1 = epics.getLast();
        assertEqualsEpic(epic, resultEpic, "Эпики не совпадают");
        assertEqualsEpic(epic1, resultEpic1, "Вторые эпики не совпадают");
        assertEquals(2, epics.size(), "в мапе не два эпика");
    }

    @Test
    @DisplayName("передавать полный список подзадач")
    void shouldGetSubTasks() {
        epic = taskManager.createEpic(new Epic("Тестовая задача", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 1, 11, 10), epic.getId()));
        SubTask subtask1 = taskManager.createSubtask(new SubTask("Тестовая задача 2, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 1, 15, 40), epic.getId()));

        ArrayList<SubTask> subtasks = taskManager.getSubtasks();

        assertEquals(2, subtasks.size(), "в мапе не две подзадачи");
    }

    @Test
    @DisplayName("передавать полный список подзадач эпика по Id")
    void shouldGetSubTasksByEpicId() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 1, 15, 10), epic.getId()));
        SubTask subtask1 = taskManager.createSubtask(new SubTask("Тестовая задача 2, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 1, 12, 10), epic.getId()));

        List<SubTask> subtasks = taskManager.getSubTaskByEpic(epic.getId());

        SubTask subtaskResult = subtasks.getFirst();
        SubTask subtaskResul1 = subtasks.getLast();
        assertEqualsSubtask(subtask, subtaskResult, "Подзадачи не совпадают");
        assertEqualsSubtask(subtask1, subtaskResul1, "Подзадачи 2 не совпадают");
        assertEquals(2, subtasks.size(), "у эпика не 2 подзадачи");
    }

    @Test
    @DisplayName("сохранять подзадачи в эпик")
    void shouldGetEpicSubtasks() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 1, 13, 10), epic.getId()));
        SubTask subtask1 = taskManager.createSubtask(new SubTask("Тестовая задача 2, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 1, 14, 10), epic.getId()));

        List<Integer> subtasks = epic.getSubTasks();

        Integer subtaskResultId = subtasks.getFirst();
        Integer subtaskResultId1 = subtasks.getLast();

        assertEquals(subtask.getId(), subtaskResultId, "id позадач не совпадают");
        assertEquals(subtask1.getId(), subtaskResultId1, "id подзадач 2 не совпадают");
        assertEquals(2, subtasks.size(), "у эпика не 2 подзадачи");
    }

    @Test
    @DisplayName("передавать задачу по Id")
    void shouldGetTaskById() {
        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW));

        Task taskResult = taskManager.getTaskByID(task.getId()).get();

        assertEqualsTask(task, taskResult, "задачи не совпадают");
    }

    @Test
    @DisplayName("передавать подзадачу по Id")
    void shouldGetSubTaskById() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));

        SubTask subTaskResult = taskManager.getSubtaskById(subtask.getId()).get();

        assertEqualsSubtask(subtask, subTaskResult, "подзадачи не совпадают");
    }

    @Test
    @DisplayName("передавать эпик по Id")
    void shouldGetEpicById() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));

        Epic epicResult = taskManager.getEpicByID(epic.getId()).get();

        assertEqualsEpic(epic, epicResult, "эпики не совпадают");
    }

    @Test
    @DisplayName("обновлять задачу в мапе")
    void shouldUpdateTaskGetAndPutToMap() {
        Task task2 = new Task("Первая задача", "Описание первой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 10, 31, 13, 10));
        Task savedTask = taskManager.createTask(task2);
        savedTask.setStatus(TaskStatus.IN_PROGRESS);
        savedTask.setTitle("Новая обновленная задача");

        taskManager.updateTask(savedTask);

        List<Task> tasks = taskManager.getTasks();
        assertEqualsTask(savedTask, tasks.getFirst(), "Задачи не совпадают");
    }

    @Test
    @DisplayName("отдавать ноль при попытке обновить несуществующую задачу")
    void shouldNotUpdateTaskWithWrongId() {
        Task taskToUpdate = new Task("Тестовая задача, заголовок обновленный", "Описание тестовой обновленной задачи",
                TaskStatus.IN_PROGRESS);

        assertNull(taskManager.updateTask(taskToUpdate));
    }

    @Test
    @DisplayName("менять статус эпика при обновлении подзадачи")
    void shouldUpdateSubTaskGetAndPutToMapAndChangeEpicStatus() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 10, 31, 15, 10), epic.getId()));
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        subtask.setTitle("Новая обновленная задача");

        taskManager.updateSubTask(subtask);

        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS, "статус эпика не обновился");
    }

    @Test
    @DisplayName("обновлять подзадачу")
    void shouldUpdateSubTask() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 10, 31, 14, 10), epic.getId()));
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        subtask.setTitle("Новая обновленная задача");

        taskManager.updateSubTask(subtask);

        assertEquals(TaskStatus.IN_PROGRESS, subtask.getStatus(), "статус эпика не обновился");
        assertEquals("Новая обновленная задача", subtask.getTitle(), "статус эпика не обновился");
    }

    @Test
    @DisplayName("обновлять статус эпика на progress при добавлении к эпику позадач с аналогичным статусом")
    void shouldUpdateEpicStatusToProgressAccordingCreateSubtask() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));

        subtask = taskManager.createSubtask(new SubTask("Тестовая подзадача, заголовок", "Описание тестовой подзадачи", TaskStatus.IN_PROGRESS, epic.getId()));

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "статус эпика не изменился");
    }

    @Test
    @DisplayName("обновлять статус эпика на done при обновлении подзадач")
    void shouldUpdateEpicStatusToDoneAndNewAccordingCreateSubtask() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));

        subtask = taskManager.createSubtask(new SubTask("Тестовая подзадача, заголовок", "Описание тестовой подзадачи", TaskStatus.DONE, epic.getId()));

        assertEquals(TaskStatus.DONE, epic.getStatus(), "статус эпика не изменился");
    }

    @Test
    @DisplayName("давать эпику без подзадач статус NEW при удалении подзадач")
    void shouldSetNewEpicStatusForEpicWithoutSubtasks() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая подзадача, заголовок", "Описание тестовой подзадачи", TaskStatus.DONE, epic.getId()));

        taskManager.deleteSubtasks();

        assertEquals(epic.getStatus(), TaskStatus.NEW, "Статусы не совпадают");
    }

    @Test
    @DisplayName("обновлять эпик в мапе")
    void shouldUpdateEpicGetAndPutToMap() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        epic.setTitle("Обновленный эпик");
        epic.setDescription("Обновленное описание эпика");

        taskManager.updateEpic(epic);

        assertEquals("Обновленный эпик", epic.getTitle(), "Заголовок эпика не обновился");
        assertEquals("Обновленное описание эпика", epic.getDescription(), "Описание эпика не обновилось");
    }

    @Test
    @DisplayName("удалять все задачи из мапы")
    void shouldDeleteTasksFromMap() {
        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 1, 1, 10)));
        Task task2 = taskManager.createTask(new Task("Тестовая задача2, заголовок", "Описание тестовой задачи2", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 1, 2, 10)));
        Integer size = taskManager.getTasks().size();

        taskManager.deleteTasks();

        Integer sizeResult = taskManager.getTasks().size();
        assertEquals(2, size, "мапа задач пуста");
        assertEquals(0, sizeResult, "мапа задач не пуста");
    }

    @Test
    @DisplayName("удалять все эпики из мапы")
    void shouldDeleteEpicsFromMap() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epic1 = taskManager.createEpic(new Epic("Первый обновлённый эпик", "Описание первого эпика"));
        Integer size = taskManager.getEpics().size();

        taskManager.deleteEpics();

        Integer sizeResult = taskManager.getEpics().size();
        assertEquals(2, size, "мапа задач пуста");
        assertEquals(0, sizeResult, "мапа задач не пуста");
    }

    @Test
    @DisplayName("удалять все подзадачи из мапы при удалении всех эпиков")
    void shouldDeleteSubtasksWhenEpicsDelete() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epic1 = taskManager.createEpic(new Epic("Первый обновлённый эпик", "Описание первого эпика"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 100, LocalDateTime.of(2024, 10, 31, 16, 10), epic.getId()));
        SubTask subtask1 = taskManager.createSubtask(new SubTask("Тестовая подзадача, заголовок", "Описание тестовой подзадачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 30, 16, 10), epic.getId()));
        Integer size = taskManager.getSubtasks().size();

        taskManager.deleteEpics();

        Integer sizeResult = taskManager.getSubtasks().size();
        assertEquals(2, size, "мапа задач пуста");
        assertEquals(0, sizeResult, "мапа задач не пуста");
        assertEquals(0, epic.getSubTasks().size(), "подзадачи у эпика удалены");
    }

    @Test
    @DisplayName("удалять все подзадачи из мапы и из эпика")
    void shouldDeleteSubtasksFromMapAndEpic() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 10, 15, 10), epic.getId()));
        SubTask subtask1 = taskManager.createSubtask(new SubTask("Тестовая подзадача, заголовок", "Описание тестовой подзадачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 11, 15, 10), epic.getId()));
        Integer size = taskManager.getSubtasks().size();

        taskManager.deleteSubtasks();

        Integer sizeResult = taskManager.getSubtasks().size();
        assertEquals(2, size, "мапа задач пуста");
        assertEquals(0, sizeResult, "мапа задач не пуста");
        assertEquals(0, epic.getSubTasks().size(), "подзадачи у эпика удалены");
    }

    @Test
    @DisplayName("удалять задачу из мапы по id")
    void shouldDeleteTaskFromMapById() {
        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 2, 15, 10)));
        Task task2 = taskManager.createTask(new Task("Тестовая задача2, заголовок", "Описание тестовой задачи2", TaskStatus.NEW, 10, LocalDateTime.of(2024, 11, 2, 16, 10)));

        taskManager.deleteTaskById(task.getId());

        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size(), "в мапе не одна задача");
        assertEqualsTask(task2, tasks.getFirst(), "задачи не совпадают");
    }

    @Test
    @DisplayName("удалять эпик из мапы по id")
    void shouldDeleteEpicFromMapByID() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epic1 = taskManager.createEpic(new Epic("Первый обновлённый эпик", "Описание первого эпика"));

        taskManager.deleteEpicById(epic.getId());

        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size(), "в мапе не один эпик");
        assertEqualsEpic(epic1, epics.getFirst(), "эпики не совпадают");
    }

    @Test
    @DisplayName("удалять все подзадачи эпика при удалении эпика по id")
    void shouldDeleteEpicAndEpicSubtasksFromMapByID() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));

        taskManager.deleteEpicById(epic.getId());

        assertEquals(0, epic.getSubTasks().size(), "подзадачи эпика не пусты");
    }

    @Test
    @DisplayName("удалять подзадачу по id из мапы")
    void shouldDeleteSubtaskById() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));

        taskManager.deleteSubtaskById(subtask.getId());

        List<SubTask> subTasks = taskManager.getSubtasks();
        assertEquals(0, subTasks.size(), "мапа подзадач не опустела");
    }

    @Test
    @DisplayName("удалять подзадачу по id из эпика")
    void shouldDeleteSubtaskByIdFromEpic() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));

        taskManager.deleteSubtaskById(subtask.getId());

        assertEquals(0, epic.getSubTasks().size(), "подзадачи эпика не пусты");
    }

    private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getTitle(), actual.getTitle(), message + " , title");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " , description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + " , status");
        assertEquals(expected.getType(), actual.getType(), message + " , type");
    }

    private static void assertEqualsEpic(Epic expected, Epic actual, String message) {
        assertEquals(expected.getTitle(), actual.getTitle(), message + " , title");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " , description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + " , status");
        assertEquals(expected.getType(), actual.getType(), message + " , type");
        assertEquals(expected.getSubTasks(), actual.getSubTasks(), message + ", subtasks");
    }

    private static void assertEqualsSubtask(SubTask expected, SubTask actual, String message) {
        assertEquals(expected.getTitle(), actual.getTitle(), message + " , title");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " , description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + " , status");
        assertEquals(expected.getType(), actual.getType(), message + " , type");
        assertEquals(expected.getEpicId(), actual.getEpicId(), message + " , epicId");
    }
}
