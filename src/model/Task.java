package model;

import type.TaskStatus;
import type.TaskType;
import utils.EnumHelper;
import utils.Identifier;

import java.util.Objects;

public class Task {
    private final int id;

    protected final String name;

    protected final String description;

    protected TaskType type;

    protected TaskStatus status;

    public Task(String name, String description, TaskStatus status) {
        id = Identifier.INSTANCE.generate();
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
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

    public TaskType getType() {
        return type;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus taskStatus) {
        this.status = taskStatus;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskType=" + EnumHelper.getTypeName(type) +
                ", status=" + EnumHelper.getStatusName(status) +
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
