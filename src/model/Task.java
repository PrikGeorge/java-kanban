package model;

import type.Status;
import type.Type;
import utils.Identifier;

import java.util.Objects;

public class Task {
    private final int id;

    protected final String name;

    protected final String description;

    protected Type type;

    protected Status status;

    public Task(String name, String description, Status status) {
        id = Identifier.INSTANCE.generate();
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = Type.TASK;
    }

    public Task(String name, String description) {
        id = Identifier.INSTANCE.generate();
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + Type.getTypeName(type) +
                ", status=" + Status.getStatusName(status) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;

        return id == task.id
                && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && type == task.type
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, type, status);
    }
}
