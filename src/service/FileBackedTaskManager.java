package service;

import converter.StringConverter;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    InMemoryTaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());

    public FileBackedTaskManager(File file) {
        super(Managers.getDefaultHistory());
        this.file = file;
    }

    public FileBackedTaskManager() {
        super(Managers.getDefaultHistory());
        this.file = new File("src/resources/File.csv");
    }

    public FileBackedTaskManager(HistoryManager historyManager) {
        super(historyManager);
        this.file = new File("src/resources/File.csv");
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackUp = new FileBackedTaskManager(file);
        fileBackUp.readFile();
        return fileBackUp;
    }

    private void readFile() {
        int maxId = 0;
        int id;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                if (line.isBlank()) {
                    break;
                }
                Task task = StringConverter.taskFromString(line);
                if (task != null) {
                    id = task.getId();
                    if (maxId < id) {
                        maxId = id;
                    }
                    if (task.getType() == TaskType.TASK) {
                        super.addTask(id, task);
                    } else if (task.getType() == TaskType.SUBTASK) {
                        super.addSubtask(id, (SubTask) task);
                    } else if (task.getType() == TaskType.EPIC) {
                        super.addEpic(id, (Epic) task);
                    }
                    taskManager.setIdCounter(maxId);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.", e);
        }
    }

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write("id,type,title,status,description,epicId\n");
            for (Task task : getTasks()) {
                bufferedWriter.write(StringConverter.taskToString(task) + "\n");
            }
            for (SubTask subTask : getSubtasks()) {
                bufferedWriter.write(StringConverter.taskToString(subTask) + "\n");
            }
            for (Epic epic : getEpics()) {
                bufferedWriter.write(StringConverter.taskToString(epic) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.", e);
        }
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public SubTask createSubtask(SubTask subtask) {
        SubTask newSubtask = super.createSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Task updateTask(Task task) {
        Task newTask = super.updateTask(task);
        save();
        return newTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subtask) {
        SubTask newSubtask = super.updateSubTask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic newEpic = super.updateEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public void deleteEpicById(Integer epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteTaskById(Integer taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteSubtaskById(Integer subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }


}
