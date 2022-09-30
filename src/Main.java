import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import type.TaskStatus;

public class Main {

    private static final TaskManager<Task> inMemoryTaskManager = Managers.getDefault();

    public static void main(String[] args) {
        testActions();
    }

    private static void testActions(){

        /** создаем эпик "магазин" */
        Epic epicShop = new Epic("Покупки в магазине", "Пойти в магазин до 18:00 и сделать покупки");

        /** создаем подзадачи для эпика "магазин" */
        Subtask subtaskBuyMilk = new Subtask("Купить молоко", "Простоквашино", TaskStatus.NEW, epicShop.getId());
        Subtask subtaskBuyBred = new Subtask("Купить хлеб", "Нарезной", TaskStatus.NEW, epicShop.getId());

        /** создаем эпик "работа" */
        Epic epicWork = new Epic("Закончить работу пораньше", "Выйти с работы до 19:00");

        /** создаем подзадачи для эпика "работа"*/
        Subtask subtaskEndProject = new Subtask("Закончить проект", "Реализовать все классы и методы", TaskStatus.NEW, epicWork.getId());

        /** добавляем задачи в таск менеджер*/
        inMemoryTaskManager.addTask(epicShop);
        inMemoryTaskManager.addTask(epicWork);
        inMemoryTaskManager.addTask(subtaskBuyMilk);
        inMemoryTaskManager.addTask(subtaskBuyBred);
        inMemoryTaskManager.addTask(subtaskEndProject);

        /** проверка истории*/
        testHistory(epicShop);
        testHistory(epicWork);
        testHistory(subtaskEndProject);
        testHistory(epicWork);
        testHistory(subtaskEndProject);
        testHistory(subtaskEndProject);
        testHistory(subtaskBuyMilk);
        testHistory(subtaskBuyBred);
        testHistory(epicShop);

        /** удаляем эпик*/
        inMemoryTaskManager.removeTaskById(epicWork.getId());

        /** выводим список всех задач*/
        System.out.println(inMemoryTaskManager.getTaskList());

        /** меняем статус подзадачи эпика "магазин" и далее выводим сам эпик */
        subtaskBuyMilk.setStatus(TaskStatus.IN_PROGRESS);
        subtaskBuyBred.setStatus(TaskStatus.DONE);
        inMemoryTaskManager.updateTask(subtaskBuyMilk);
        inMemoryTaskManager.updateTask(subtaskBuyBred);

        System.out.println(inMemoryTaskManager.getTaskList());

        /** удаляем одну из задач и эпик */
        inMemoryTaskManager.removeTaskById(subtaskBuyMilk.getId());

        System.out.println(inMemoryTaskManager.getTaskList());

        /** удаляем все задача и смотрим историю */
        inMemoryTaskManager.removeTaskList();
        System.out.println(inMemoryTaskManager.history());
    }

    private static <T extends Task> void testHistory(T task) {
        System.out.println("Просмотрена задача (id): " + inMemoryTaskManager.getTaskById(task.getId()).getId() + "\n"
                + inMemoryTaskManager.history());

    }

}
