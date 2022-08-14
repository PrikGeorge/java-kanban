package manager;

import model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager<T> {

    private static final int MAX_SIZE = 10;
    private final List<T> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public void add(T task) {
        if (history.size() == MAX_SIZE) {
            history.remove(0);
        }

        history.add(task);
    }

    @Override
    public List<T> getHistory() {
        if (!history.isEmpty()) {
            return history;
        }

        System.out.println("История просмотров пустая.");
        return Collections.emptyList();
    }

}
