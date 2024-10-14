package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Managers должен")
public class ManagersTest<T extends TaskManager> {
    TaskManager taskManager;

    @Test
    @DisplayName("должен создавать объект класса task")
    void shouldCreateTaskManager() {
        taskManager = Managers.getDefault();

        assertNotNull(taskManager);
    }

    @Test
    @DisplayName("должен создавать объект класса history")
    void shouldCreateHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager);
    }

}
