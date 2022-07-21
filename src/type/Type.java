package type;

public enum Type {
    TASK,
    EPIC,
    SUBTASK;

    public static String getTypeName(Type type) {
        String name = "Некорректный тип";
        if (type != null) {
            switch (type) {
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
