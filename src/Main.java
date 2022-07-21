import manager.TaskManagerImpl;
import model.Epic;
import model.Subtask;
import model.Task;
import type.Status;

public class Main {
    public static void main(String[] args) {
        TaskManagerImpl<Task> taskManagerImpl = new TaskManagerImpl<>();

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
        taskManagerImpl.addTask(epicShop);
        taskManagerImpl.addTask(epicWork);
        taskManagerImpl.addTask(subtaskBuyMilk);
        taskManagerImpl.addTask(subtaskBuyBred);
        taskManagerImpl.addTask(subtaskEndProject);

        // выводим список всех задач
        System.out.println(taskManagerImpl.getTaskList());

        // меняем статус подзадачи эпика "магазин" и далее выводим сам эпик
        subtaskBuyMilk.setStatus(Status.IN_PROGRESS);
        subtaskEndProject.setStatus(Status.DONE);
        taskManagerImpl.updateTask(subtaskBuyMilk);
        taskManagerImpl.updateTask(subtaskEndProject);

        System.out.println(taskManagerImpl.getTaskList());

        // удаляем одну из задач и эпик
        taskManagerImpl.removeTaskById(epicWork.getId());
        taskManagerImpl.removeTaskById(subtaskBuyMilk.getId());

        System.out.println(taskManagerImpl.getTaskList());
    }
}
