package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryTaskManager должен ")
public class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager;

    Task task;
    Epic epic;
    SubTask subtask;

    @BeforeEach
    void init() {
        EmptyHistoryManager historyManager = new EmptyHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

//    @Test
//    @DisplayName("присваивать id задачи, эпика, подзадачи")
//    void shouldSetTaskId() {
//        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW));
//        int idCounterExpected = task.getId() + 1;
//        int idCounterResult = taskManager.getIdCounter();
//        assertEquals(idCounterResult, idCounterExpected);
//    }


    @Test
    @DisplayName("корректно передавать в мапу и из мапы созданную задачу")
    void shouldCreateTaskGetAndPutToMap() {
        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW));
        Task taskResult2 = taskManager.createTask(task);
        assertEqualsTask(taskManager.tasks.get(taskResult2.getId()), task, "задачи не совпадают");
    }

    @Test
    @DisplayName("корректно передавать в мапу и из мапы созданный эпик")
    void shouldCreateEpicGetAndPutToMap() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epicResult = taskManager.createEpic(epic);
        assertEqualsEpic(taskManager.epics.get(epicResult.getId()), epic, "эпики не совпадают");
    }

    @Test
    @DisplayName("передавать в мапу и из мапы созданную подзадачу")
    void shouldCreateSubtaskGetAndPutToMap() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));
        SubTask subtaskResult = taskManager.createSubtask(subtask);
        assertEqualsSubtask(taskManager.subtasks.get(subtaskResult.getId()), subtask, "подзадачи на совпадают");
    }

    @Test
    @DisplayName("добавлять подзадачу в эпик при создании подзадачи")
    void shouldAddCreatedSubtaskToEpic() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));
        ArrayList<Integer> epicSubtasks = epic.getSubTasks();
        assertEqualsSubtask(taskManager.subtasks.get(epicSubtasks.getFirst()), subtask, "подзадачи не совпадают");
    }

    @Test
    @DisplayName("отдавать ноль при попытке обновить несуществующую задачу")
    void shouldNotCreateSubTaskWithWrongEpicId() {
        subtask = new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, 80);
        assertNull(taskManager.createSubtask(subtask), "подзадача с несуществующим эпиком создалась");
        assertNull(taskManager.subtasks.get(subtask.getId()), "подзадача с несуществующим эпиком добавилась в мапу");
    }

    @Test
    @DisplayName("передавать историю просмотра задач")
    void shouldReturnHistoryFromHistoryManager() {
      task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW));
        taskManager = new InMemoryTaskManager(new HistoryManager() {
            @Override
            public void add(Task task) {
            }

            @Override
            public List<Task> getAll() {
                return Collections.singletonList(task);
            }
        });

        taskManager.tasks.put(task.getId(), task); //здесь не стала использовать другой метод, чтобы не усложнять тест
        List<Task> all = taskManager.getHistory();

        assertNotNull(all, "история пуста");
        assertEquals(all.size(), 1, "в истории не 1 задача");
        assertEqualsTask(all.getFirst(), taskManager.tasks.get(task.getId()), "задача не верно добавлена в историю");
    }

    @Test
    @DisplayName("передавать полный список задач")
    void shouldGetTasks() {
        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW));
        Task task1 = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW));
        Task taskExpected = new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW);
        Task taskExpected1 = new Task( "Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW);
        ArrayList<Task> tasksResult2 = taskManager.getTasks();
        Task taskResult = tasksResult2.getFirst();
        Task taskResult1 = tasksResult2.get(1);
        assertEqualsTask(taskExpected, taskResult, "задачи не совпадают");
        assertEqualsTask(taskExpected1, taskResult1, "Вторые задачи не совпадают");
        assertEquals(tasksResult2.size(), 2, "в мапе не две задачи");
    }

    @Test
    @DisplayName("передавать полный список эпиков")
    void shouldGetEpics() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epic1 = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epicExpected = new Epic("Тестовая задача, заголовок", "Описание тестовой задачи");
        Epic epicExpected1 = new Epic("Тестовая задача, заголовок", "Описание тестовой задачи");
        ArrayList<Epic> epicResult2 = taskManager.getEpics();
        Epic resultEpic = epicResult2.getFirst();
        Epic resultEpic1 = epicResult2.get(1);
        assertEqualsEpic(epicExpected, resultEpic, "Эпики не совпадают");
        assertEqualsEpic(epicExpected1, resultEpic1, "Вторые эпики не совпадают");
        assertEquals(epicResult2.size(), 2, "в мапе не два эпика");
    }

    @Test
    @DisplayName("передавать полный список подзадач")
    void shouldGetSubTasks() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epic1 = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));
        SubTask subtask1 = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic1.getId()));
        SubTask subtaskExpected = new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId());
        SubTask subtaskExpected1 = new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic1.getId());
        ArrayList<SubTask> subtaskResult2 = taskManager.getSubtasks();
        assertEqualsSubtask(subtaskExpected, subtaskResult2.getFirst(), "Подзадачи не совпадают");
        assertEqualsSubtask(subtaskExpected1, subtaskResult2.get(1), "Вторые подзадачи не совпадают");
        assertEquals(subtaskResult2.size(), 2, "в мапе не две подзадачи");
    }

    @Test
    @DisplayName("передавать полный список подзадач эпика по Id")
    void shouldGetSubTasksByEpicId() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epic1 = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));
        SubTask subtask1 = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic1.getId()));
        SubTask subtask2 = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic1.getId()));

        ArrayList<SubTask> subtasksResult1 = taskManager.getSubTaskByEpic(epic.getId());
        ArrayList<SubTask> subtasksResult2 = taskManager.getSubTaskByEpic(epic1.getId());

        SubTask subtaskResult = subtasksResult1.getFirst();
        SubTask subtaskResul1 = subtasksResult2.getFirst();
        SubTask subtaskResult2 = subtasksResult2.get(1);

        assertEqualsSubtask(subtask, subtaskResult, "Подзадачи не совпадают");
        assertEqualsSubtask(subtask1, subtaskResul1, "Подзадачи не совпадают");
        assertEqualsSubtask(subtask2, subtaskResult2, "Вторые подзадачи не совпадают");
        assertEquals(subtasksResult1.size(), 1, "у эпика не 1 подзадача");
        assertEquals(subtasksResult2.size(), 2, "у эпика не 2 подзадачи");
    }

    @Test
    @DisplayName("передавать задачу по Id")
    void shouldGetTaskById() {
        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW));
        Task taskResult = taskManager.getTaskByID(task.getId());
        assertEqualsTask(task, taskResult, "задачи не совпадают");
    }

    @Test
    @DisplayName("передавать подзадачу по Id")
    void shouldGetSubTaskById() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));
        SubTask subTaskResult = taskManager.getSubtaskById(subtask.getId());
        assertEqualsSubtask(subtask, subTaskResult, "подзадачи не совпадают");
    }

    @Test
    @DisplayName("передавать эпик по Id")
    void shouldGetEpicById() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epicResult = taskManager.getEpicByID(epic.getId());
        assertEqualsEpic(epic, epicResult, "эпики не совпадают");
    }

    @Test
    @DisplayName("обновлять задачу в мапе")
    void shouldUpdateTaskGetAndPutToMap() {
        Task task2 = new Task("Первая задача", "Описание первой задачи", TaskStatus.NEW);
        Task savedTask = taskManager.createTask(task2);
        savedTask.setStatus(TaskStatus.IN_PROGRESS);
        savedTask.setTitle("Новая обновленная задача");
        taskManager.updateTask(savedTask);
        assertEqualsTask(taskManager.tasks.get(savedTask.getId()), savedTask, "Задачи не совпадают");
    }

    @Test
    @DisplayName("отдавать ноль при попытке обновить несуществующую задачу")
    void shouldNotUpdateTaskWithWrongId() {
        Task taskToUpdate = new Task("Тестовая задача, заголовок обновленный", "Описание тестовой обновленной задачи",
                TaskStatus.IN_PROGRESS);
        assertNull(taskManager.updateTask(taskToUpdate));
        assertNull(taskManager.tasks.get(taskToUpdate.getId()));
    }

    @Test
    @DisplayName("обновлять подзадачу в мапе и менять статус эпика")
    void shouldUpdateSubTaskGetAndPutToMapAndChangeEpicStatus() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        SubTask subtask3 = new SubTask("Подзадача 2 первого эпика", "Описание подзадачи 2 первого эпика", TaskStatus.IN_PROGRESS, epic.getId());
        SubTask savedSubtask = taskManager.createSubtask(subtask3);
        savedSubtask.setStatus(TaskStatus.IN_PROGRESS);
        savedSubtask.setTitle("Новая обновленная задача");
        savedSubtask.setDescription("Обновленное описание");
        taskManager.updateSubTask(savedSubtask);
        assertEqualsSubtask(taskManager.subtasks.get(subtask3.getId()), savedSubtask, "Задачи не совпадают");
        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("обновлять статус эпика на progress при добавлении к эпику позадач с аналогичным статусом")
    void shouldUpdateEpicStatusToProgressAccordingCreateSubtask() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая подзадача, заголовок", "Описание тестовой подзадачи", TaskStatus.IN_PROGRESS, epic.getId()));
        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS, "Статусы не совпадают");
    }

    @Test
    @DisplayName("обновлять статус эпика на done при обновлении подзадач")
    void shouldUpdateEpicStatusToDoneAndNewAccordingCreateSubtask() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая подзадача, заголовок", "Описание тестовой подзадачи", TaskStatus.DONE, epic.getId()));
        assertEquals(epic.getStatus(), TaskStatus.DONE, "Статусы не совпадают");
    }

    @Test
    @DisplayName("обновлять статус эпика в зависимости от статуса новых подзадач")
    void shouldUpdateEpicStatusAccordingCreateSubtask() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая подзадача, заголовок", "Описание тестовой подзадачи", TaskStatus.DONE, epic.getId()));
        TaskStatus epicStatusDone = epic.getStatus();
        SubTask subtask1 = taskManager.createSubtask(new SubTask("Тестовая подзадача, заголовок", "Описание тестовой подзадачи", TaskStatus.NEW, epic.getId()));
        assertEquals(epic.getStatus(), TaskStatus.IN_PROGRESS, "Статусы совпадают");
        assertEquals(epicStatusDone, TaskStatus.DONE, "Статусы не совпадают");
    }


    @Test
    @DisplayName("давать эпику без подзадач статус NEW")
    void shouldSetNewEpicStatusForEpicWithoutSubtasks() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        assertEquals(epic.getStatus(), TaskStatus.NEW, "Статусы не совпадают");
    }


    @Test
    @DisplayName("обновлять эпик в мапе")
    void shouldUpdateEpicGetAndPutToMap() {
        Epic epic2 = new Epic("Второй эпик", "Описание второго эпика");
        Epic savedEpic = taskManager.createEpic(epic2);
        savedEpic.setStatus(TaskStatus.IN_PROGRESS);
        savedEpic.setTitle("Обновленный эпик");
        savedEpic.setDescription("Обновленное описание эпика");
        taskManager.updateEpic(savedEpic);
        assertEqualsEpic(taskManager.epics.get(savedEpic.getId()), savedEpic, "Эпики не совпадают");
    }

    @Test
    @DisplayName("удалять все задачи из мапы")
    void shouldDeleteTasksFromMap() {
        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW));
        Task task2 = taskManager.createTask(new Task("Тестовая задача2, заголовок", "Описание тестовой задачи2", TaskStatus.NEW));
        taskManager.deleteTasks();
        assertEquals(taskManager.tasks.values().size(), 0, "мапа задач не пуста");
    }

    @Test
    @DisplayName("удалять все эпики из мапы")
    void shouldDeleteEpicsFromMap() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epic1 = taskManager.createEpic(new Epic("Первый обновлённый эпик", "Описание первого эпика"));
        taskManager.deleteEpics();
        assertEquals(taskManager.epics.values().size(), 0, "мапа эпиков не пуста");
    }

    @Test
    @DisplayName("удалять все подзадачи из мапы и из эпика")
    void shouldDeleteSubtasksFromMapAndEpic() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));
        SubTask subtask1 = taskManager.createSubtask(new SubTask("Тестовая подзадача, заголовок", "Описание тестовой подзадачи", TaskStatus.NEW, epic.getId()));
        taskManager.deleteSubtasks();
        assertEquals(taskManager.subtasks.values().size(), 0, "мапа подзадач не пуста");
        assertEquals(epic.getSubTasks().size(), 0, "список подзадач эпика не пуст");
    }

    @Test
    @DisplayName("удалять задачу из мапы по id")
    void shouldDeleteTaskFromMapById() {
        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW));
        Task task2 = taskManager.createTask(new Task("Тестовая задача2, заголовок", "Описание тестовой задачи2", TaskStatus.NEW));
        taskManager.deleteTaskByID(task.getId());
        assertEquals(taskManager.tasks.values().size(), 1, "в мапе не одна задача");
        assertEqualsTask(task2, taskManager.tasks.get(task2.getId()), "задачи не совпадают");
        assertNull(taskManager.tasks.get(task.getId()));
    }

    @Test
    @DisplayName("удалять эпик из мапы по id и удалять все подзадачи эпика")
    void shouldDeleteEpicAndEpicSubtasksFromMapByID() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        Epic epic1 = taskManager.createEpic(new Epic("Первый обновлённый эпик", "Описание первого эпика"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));
        SubTask subtask1 = taskManager.createSubtask(new SubTask("Тестовая подзадача, заголовок", "Описание тестовой подзадачи", TaskStatus.NEW, epic.getId()));
        taskManager.deleteEpicByID(epic.getId());
        assertEquals(taskManager.epics.values().size(), 1, "в мапе не один эпик");
        assertEqualsEpic(epic1, taskManager.epics.get(epic1.getId()), "эпики не совпадают");
        assertNull(taskManager.epics.get(epic.getId()));
        assertNull(taskManager.subtasks.get(subtask.getId()));
        assertNull(taskManager.subtasks.get(subtask1.getId()));
    }

    @Test
    @DisplayName("удалять все подзадачи из мапы и из эпика")
    void shouldDeleteSubtaskFromMapAndEpicById() {
        epic = taskManager.createEpic(new Epic("Тестовая задача, заголовок", "Описание тестовой задачи"));
        subtask = taskManager.createSubtask(new SubTask("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW, epic.getId()));
        SubTask subtask1 = taskManager.createSubtask(new SubTask("Тестовая подзадача, заголовок", "Описание тестовой подзадачи", TaskStatus.NEW, epic.getId()));
        ArrayList<Integer> epicSubtasks = epic.getSubTasks();
        taskManager.deleteSubtaskByID(subtask.getId());
        assertEquals(taskManager.subtasks.values().size(), 1, "в мапе больше одной подзадачи");
        assertEqualsSubtask(subtask1, taskManager.subtasks.get(subtask1.getId()), "подзадачи не совпадают");
        assertEquals(epicSubtasks.size(), 1, "в списке подзадач больше одной задачи или нет задач");
        assertNull(taskManager.subtasks.get(subtask.getId()), "удаленная подзадача есть в мапе");
        assertFalse(epicSubtasks.contains(subtask.getId()), "поздача осталась у эпика");
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

    private static class EmptyHistoryManager implements HistoryManager {
        @Override
        public void add(Task task) {
        }

        @Override
        public List<Task> getAll() {
            return Collections.emptyList();
        }
    }
}
