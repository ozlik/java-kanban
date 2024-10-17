package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryTaskManager должен ")
public class InMemoryTaskManagerTest extends ManagersTest<InMemoryTaskManager> {
    InMemoryTaskManager taskManager;
    EmptyHistoryManager historyManager;
    Task task;

    @Override
    InMemoryTaskManager createManager() {
        return new InMemoryTaskManager(new EmptyHistoryManager());
    }

    @BeforeEach
    void init() {
        historyManager = new EmptyHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    @Test
    @DisplayName("должен создавать объект класса history")
    void shouldCreateHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }

    @Test
    @DisplayName("передавать историю просмотра задач")
    void shouldReturnHistoryFromHistoryManager() {
        task = taskManager.createTask(new Task("Тестовая задача, заголовок", "Описание тестовой задачи", TaskStatus.NEW));
        taskManager = new InMemoryTaskManager(new HistoryManager() {
            @Override
            public void add(Task task) {}

            @Override
            public List<Task> getAll() {
                return Collections.singletonList(task);
            }

            @Override
            public void remove(int id) {}
        }
        );

        taskManager.tasks.put(task.getId(), task); //здесь не стала использовать другой метод, чтобы не усложнять тест
        List<Task> all = taskManager.getHistory();

        assertNotNull(all, "история пуста");
        assertEquals(all.size(), 1, "в истории не 1 задача");
        assertEqualsTask(all.getFirst(), taskManager.tasks.get(task.getId()), "задача не верно добавлена в историю");
    }

    private static class EmptyHistoryManager implements HistoryManager {
        @Override
        public void add(Task task) {
        }

        @Override
        public List<Task> getAll() {
            return Collections.emptyList();
        }

        @Override
        public void remove(int id) {

        }
    }

    private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getTitle(), actual.getTitle(), message + " , title");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " , description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + " , status");
        assertEquals(expected.getType(), actual.getType(), message + " , type");
    }
}
