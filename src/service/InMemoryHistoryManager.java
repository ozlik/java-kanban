package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    List<Task> tasksHistory;

    @Override
    public List<Task> getAll() {
        return List.copyOf(tasksHistory);
    }

    @Override
    public void add(Task task) {
        if (tasksHistory == null) {
            tasksHistory = new ArrayList<>();
        } else if (tasksHistory.size() == 10) {
            tasksHistory.removeFirst();
        }
        tasksHistory.add(task);

    }
}
