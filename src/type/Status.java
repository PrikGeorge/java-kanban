package type;

public enum Status {
    NEW,
    IN_PROGRESS,
    DONE;


    public static String getStatusName(Status status) {
        String name = "Некорректный статус";
        if (status != null) {
            switch (status) {
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
}
