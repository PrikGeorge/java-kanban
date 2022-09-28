package manager;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T> {

    private Node<T> head = null;

    private Node<T> tail = null;

    private Map<Integer, Node<T>> nodeMap = new HashMap<>();

    private int size = 0;

    @Override
    public void add(T task) {
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public List<T> getHistory() {
        List<T> tasks = getTasks();
        if (!tasks.isEmpty()) {
            return tasks;
        }

        System.out.println("История просмотров пустая.");
        return Collections.emptyList();
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            removeNode(nodeMap.get(id));
            nodeMap.remove(id);
        }
    }

    private void linkLast(T task) {
        Node<T> newNode = new Node<T>(task);

        if (size == 0) {
            head = newNode;
            tail = head;
        } else {

            if (tail != null) {
                tail.setNext(newNode);
                newNode.setPrev(tail);
            }
            tail = newNode;
        }

        nodeMap.put(task.getId(), newNode);
        size++;
    }

    private void removeNode(Node<T> node) {

        if (node != head) {
            if (node.getPrev() != null) {
                node.getPrev().setNext(node.getNext());
            }
        } else {
            head = node.getNext();
        }

        if (node != tail) {
            node.getNext().setPrev(node.getPrev());
        } else {
            tail = node.getPrev();
        }

        size--;
    }

    private List<T> getTasks() {

        List<T> tasks = new ArrayList<>();

        if (head != null) {
            Node<T> currentNode = head;
            do {
                tasks.add(currentNode.getTask());
                currentNode = currentNode.getNext();
            }
            while (currentNode != null);
        }

        return tasks;
    }

}
