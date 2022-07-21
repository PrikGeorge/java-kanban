package model;

import type.Status;
import type.Type;

public class Subtask extends Task {
    private final Epic parent;

    public Subtask(String name, String description, Status status, Epic parent) {
        super(name, description, status);
        this.type = Type.SUBTASK;
        this.parent = parent;
    }

    public Epic getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", parent.id=" + parent.getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + Type.getTypeName(type) + '\'' +
                ", status='" + Status.getStatusName(status) + '\'' +
                '}';
    }
}
