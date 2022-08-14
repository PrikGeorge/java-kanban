import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import type.Status;

public class Main {

    private static final TaskManager<Task> inMemoryTaskManager = Managers.getDefault();

    public static void main(String[] args) {


        // создаем эпик "магазин"
        Epic epicShop = new Epic("Покупки в магазине", "Пойти в магазин до 18:00 и сделать покупки");

        // создаем подзадачи для эпика "магазин"
        Subtask subtaskBuyMilk = new Subtask("Купить молоко", "Простоквашино", Status.NEW, epicShop);
        Subtask subtaskBuyBred = new Subtask("Купить хлеб", "Нарезной", Status.NEW, epicShop);

        // создаем эпик "работа"
        Epic epicWork = new Epic("Закончить работу пораньше", "Выйти с работы до 19:00");

        // создаем подзадачи для эпика "работа"
        Subtask subtaskEndProject = new Subtask("Закончить проект", "Реализовать все классы и методы", Status.NEW, epicWork);

        // добавляем задачи в таск менеджер
        inMemoryTaskManager.addTask(epicShop);
        inMemoryTaskManager.addTask(epicWork);
        inMemoryTaskManager.addTask(subtaskBuyMilk);
        inMemoryTaskManager.addTask(subtaskBuyBred);
        inMemoryTaskManager.addTask(subtaskEndProject);

        // проверка истории
        testHistory(epicShop);
        testHistory(epicWork);
        testHistory(epicWork);
        testHistory(epicWork);
        testHistory(subtaskBuyMilk);
        testHistory(subtaskBuyBred);
        testHistory(subtaskEndProject);
        testHistory(subtaskEndProject);
        testHistory(subtaskEndProject);
        testHistory(epicShop);
        testHistory(subtaskEndProject);
        testHistory(epicShop);
        testHistory(epicShop);
        testHistory(epicShop);

        // выводим список всех задач
        System.out.println(inMemoryTaskManager.getTaskList());

        // меняем статус подзадачи эпика "магазин" и далее выводим сам эпик
        subtaskBuyMilk.setStatus(Status.IN_PROGRESS);
        subtaskEndProject.setStatus(Status.DONE);
        inMemoryTaskManager.updateTask(subtaskBuyMilk);
        inMemoryTaskManager.updateTask(subtaskEndProject);

        System.out.println(inMemoryTaskManager.getTaskList());

        // удаляем одну из задач и эпик
        inMemoryTaskManager.removeTaskById(epicWork.getId());
        inMemoryTaskManager.removeTaskById(subtaskBuyMilk.getId());

        System.out.println(inMemoryTaskManager.getTaskList());
    }

    public static <T extends Task> void testHistory(T task) {
        System.out.println("Просмотрена задача (id): " + inMemoryTaskManager.getTaskById(task.getId()).getId() + "\n"
                + inMemoryTaskManager.history());

    }
}
