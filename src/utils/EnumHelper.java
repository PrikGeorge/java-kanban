package utils;

import type.TaskStatus;
import type.TaskType;

public class EnumHelper {

    public static String getStatusName(TaskStatus taskStatus) {
        String name = "Некорректный статус";
        if (taskStatus != null) {
            switch (taskStatus) {
                case NEW:
                    name = "Новая";
                    break;
                case IN_PROGRESS:
                    name = "В процессе";
                    break;
                case DONE:
                    name = "Завершена";
                    break;
                default:
                    name = "Некорректный статус";
            }
        }
        return name;
    }

    public static String getTypeName(TaskType taskType) {
        String name = "Некорректный тип";
        if (taskType != null) {
            switch (taskType) {
                case EPIC:
                    name = "Эпик";
                    break;
                case TASK:
                    name = "Задача";
                    break;
                case SUBTASK:
                    name = "Подзадача";
                    break;
                default:
                    name = "Некорректный тип";
            }
        }
        return name;
    }
}
