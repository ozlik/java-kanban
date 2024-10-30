package converter;

import model.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StringConverter {
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Task taskFromString(String line) {
        String[] splitTask;
        splitTask = line.split(",");
        int id = Integer.parseInt(splitTask[0]);
        TaskType type = TaskType.valueOf(splitTask[1]);
        String title = splitTask[2];
        TaskStatus status = TaskStatus.valueOf(splitTask[3]);
        String description = splitTask[4];
        int duration = Integer.parseInt(splitTask[5]);
        LocalDateTime startTime = LocalDateTime.parse(splitTask[6], formatter);
        LocalDateTime endTime = LocalDateTime.parse(splitTask[7], formatter);
        switch (type) {
            case SUBTASK -> {
                int epicId = Integer.parseInt(splitTask[8]);
                SubTask subTask = new SubTask(title, description, status, duration, startTime, epicId);
                subTask.setId(id);
                return subTask;
            }
            case TASK -> {
                Task task = new Task(title, description, status, duration, startTime);
                task.setId(id);
                return task;
            }
            case EPIC -> {
                List<Integer> epicSubtasks = new ArrayList<>();
                for (int i = 8; i < splitTask.length; i++) {
                    epicSubtasks.add(Integer.parseInt(splitTask[i]));
                }
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setStatus(status);
                epic.setSubTasks(epicSubtasks);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                epic.setEndTime(endTime);
                return epic;
            }
            default -> System.out.println("Что-то пошло не так при формировании задачи из строки");
        }
        return null;
    }

    public static String taskToString(Task task) {
        String type = String.valueOf(task.getType());
        return switch (type) {
            case "EPIC", "TASK" -> String.join(",", Integer.toString(task.getId()), type, task.getTitle(),
                    task.getStatus().toString(), task.getDescription(), Integer.toString(task.getDuration()),
                    task.getStartTime().format(formatter), task.getEndTime().format(formatter));
            case "SUBTASK" -> String.join(",", Integer.toString(task.getId()), type, task.getTitle(),
                    task.getStatus().toString(), task.getDescription(), Integer.toString(task.getDuration()),
                    task.getStartTime().format(formatter), task.getEndTime().format(formatter), Integer.toString(((SubTask) task).getEpicId()));
            default -> null;
        };
    }
}
