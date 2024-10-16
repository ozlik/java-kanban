package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileBackedTaskManagerTest должен ")
public class FileBackedTaskManagerTest extends ManagersTest<FileBackedTaskManager> {
    File file;
    FileBackedTaskManager fileManager;

    @Override
    FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(new File("testFile.csv"));
    }

    @BeforeEach
    void init() {
        file = new File("testFile.csv");
        fileManager = new FileBackedTaskManager(file);

    }

    @Test
    @DisplayName("сохранять пустой файл")
    public void shouldSaveEmptyListOfTask() {

        assertEquals(Collections.emptyList(), fileManager.getTasks());
    }

    @Test
    @DisplayName("загружать пустой файл")
    public void shouldLoadEmptyListOfTask() {
        fileManager.deleteEpics();
        fileManager.deleteTasks();
        FileBackedTaskManager loadEmptyFileManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(Collections.emptyList(), loadEmptyFileManager.getTasks());
    }

    @Test
    @DisplayName("загружать список задач из файла")
    public void shouldLoadListOfTask() {
        Task task0 = fileManager.createTask(new Task("Заголовок", "Описание тестовой задачи", TaskStatus.NEW));
        Task task1 = fileManager.createTask(new Task("Заголовок 2", "Описание тестовой задачи2", TaskStatus.NEW));

        FileBackedTaskManager loadFileManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(loadFileManager.getTasks().size(), 2);
        assertEqualsTask(task0, loadFileManager.getTaskByID(task0.getId()), "Задачи не совпадают");
        assertEqualsTask(task1, loadFileManager.getTaskByID(task1.getId()), "Задачи не совпадают");
    }

    private static void assertEqualsTask(Task expected, Task actual, String message) {
        assertEquals(expected.getTitle(), actual.getTitle(), message + " , title");
        assertEquals(expected.getDescription(), actual.getDescription(), message + " , description");
        assertEquals(expected.getStatus(), actual.getStatus(), message + " , status");
        assertEquals(expected.getType(), actual.getType(), message + " , type");
    }


}