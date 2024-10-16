package converter;

import model.*;

import java.util.ArrayList;
import java.util.List;

public class StringConverter {

    public static Task taskFromString(String line) {
        String[] splitTask;
        splitTask = line.split(",");
        int id = Integer.parseInt(splitTask[0]);
        TaskType type = TaskType.valueOf(splitTask[1]);
        String title = splitTask[2];
        TaskStatus status = TaskStatus.valueOf(splitTask[3]);
        String description = splitTask[4];

        switch (type) {
            case SUBTASK -> {
                int epicId = Integer.parseInt(splitTask[5]);
                SubTask subTask = new SubTask(title, description, status, epicId);
                subTask.setId(id);
                return subTask;
            }
            case TASK -> {
                Task task = new Task(title, description, status);
                task.setId(id);
                return task;
            }
            case EPIC -> {
                List<Integer> epicSubtasks = new ArrayList<>();
                for (int i = 5; i < splitTask.length; i++) {
                    epicSubtasks.add(Integer.parseInt(splitTask[i]));
                }
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setStatus(status);
                epic.setSubTasks(epicSubtasks);
                return epic;
            }
            default -> System.out.println("Что-то пошло не так при формировании задачи из строки");
        }
        return null;
    }

    public static String taskToString(Task task) {
        String type = String.valueOf(task.getType());
        switch (type) {
            case "EPIC":
                List<Integer> epicSub = ((Epic) task).getSubTasks();
                StringBuilder stringBuilder = new StringBuilder();
                for (Integer integer : epicSub) {
                    stringBuilder.append(integer + ",");
                }
                String epicSubtasks = stringBuilder.toString();
//                String epicSubtasks = String.join(",", epicSub.toString());
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
}
