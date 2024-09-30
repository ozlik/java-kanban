package service;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    Map<Integer, Node> nodeMap = new HashMap<>();
    Node head;
    Node tail;

    static class Node {
        private Task task;
        private Node next;
        private Node prev;

        public Node(Task task) {
            this.task = task;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }
    }

    @Override
    public List<Task> getAll() {
        return new ArrayList<>(getTasks());
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node element = nodeMap.get(id);
        removeNode(element);
    }

    public void linkLast(Task task) {
        Node element = new Node(task);
        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId()));
        }
        if (head == null) {
            tail = element;
            head = element;
            element.setNext(null);
            element.setPrev(null);
        } else {
            element.setPrev(tail);
            element.setNext(null);
            tail.setNext(element);
            tail = element;
        }
        nodeMap.put(task.getId(), element);
    }

    public void removeNode(Node node) {
        if (node != null) {
            nodeMap.remove(node.getTask().getId());
            Node prev = node.getPrev();
            Node next = node.getNext();

            if (head == node) {
                head = node.getNext();
            }
            if (tail == node) {
                tail = node.getPrev();
            }
            if (prev != null) {
                prev.setNext(next);
            }
            if (next != null) {
                next.setPrev(prev);
            }
        }
    }

    List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        Node element = head;
        while (element != null) {
            taskList.add(element.getTask());
            element = element.getNext();
        }
        return taskList;
    }

}
