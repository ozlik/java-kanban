package service;

import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this(Managers.getDefaultHistory(), file);
    }

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
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
                Task task = fromString(line);
                if (task != null) {
                    id = task.getId();
                    if (maxId < id) {
                        maxId = id;
                    }
                    if (task.getType() == TaskType.TASK) {
                        putTask(id, task);
                    } else if (task.getType() == TaskType.SUBTASK) {
                        putSubtask(id, (SubTask) task);
                    } else if (task.getType() == TaskType.EPIC) {
                        putEpic(id, (Epic) task);
                    }
                    InMemoryTaskManager.setIdCounter(maxId);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время чтения файла.", e);
        }
    }


    public static Task fromString(String line) {
        String[] splitTask;
        splitTask = line.split(",");
        int id = Integer.parseInt(splitTask[0]);
        TaskType type = TaskType.valueOf(splitTask[1]);
        String title = splitTask[2];
        TaskStatus status = TaskStatus.valueOf(splitTask[3]);
        String description = splitTask[4];

        if (type == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(splitTask[5]);
            SubTask subTask = new SubTask(title, description, status, epicId);
            subTask.setId(id);
            return subTask;
        } else if (type == TaskType.TASK) {
            Task task = new Task(title, description, status);
            task.setId(id);
            return task;
        } else if (type == TaskType.EPIC) {
            List<Integer> epicSubtasks = new ArrayList<>();
            for (int i = 5; i < splitTask.length; i++) {
                epicSubtasks.add(Integer.parseInt(splitTask[i]));
            }
            Epic epic = new Epic(title, description);
            epic.setId(id);
            epic.setStatus(status);
            epic.setSubTasks(epicSubtasks);
            return epic;
        } else {
            System.out.println("Что-то пошло не так при формировании задачи из строки");
        }
        return null;
    }

    public void save() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write("id,type,title,status,description,epicId\n");
            for (Task task : getTasks()) {
                bufferedWriter.write(toString(task) + "\n");
            }
            for (SubTask subTask : getSubtasks()) {
                bufferedWriter.write(toString(subTask) + "\n");
            }
            for (Epic epic : getEpics()) {
                bufferedWriter.write(toString(epic) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи файла.", e);
        }
    }

    public String toString(Task task) {
        String type = String.valueOf(task.getType());
        switch (type) {
            case "EPIC":
                List<Integer> epicSub = ((Epic) task).getSubTasks();
                StringBuilder stringBuilder = new StringBuilder();
                for (Integer integer : epicSub) {
                    stringBuilder.append(integer + ",");
                }
                String epicSubtasks = stringBuilder.toString();
                return String.join(",", Integer.toString(task.getId()), "EPIC", task.getTitle(),
                        task.getStatus().toString(), task.getDescription(), epicSubtasks);
            case "SUBTASK":
                return String.join(",", Integer.toString(task.getId()), "SUBTASK", task.getTitle(),
                        task.getStatus().toString(), task.getDescription(), Integer.toString(((SubTask) task).getEpicId()));
            case "TASK":
                return String.join(",", Integer.toString(task.getId()), "TASK", task.getTitle(),
                        task.getStatus().toString(), task.getDescription());
            default:
                return null;
        }
    }

    private Task putTask(Integer id, Task task) {
        return super.addTask(id, task);
    }

    private Epic putEpic(Integer id, Epic epic) {
        return super.addEpic(id, epic);
    }

    private SubTask putSubtask(Integer id, SubTask subtask) {
        return super.addSubtask(id, subtask);
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
