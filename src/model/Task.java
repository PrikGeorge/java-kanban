package model;

import type.TaskStatus;
import type.TaskType;
import utils.EnumHelper;
import utils.Identifier;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private final int id;

    protected String name;

    protected final String description;

    protected TaskType type;

    protected TaskStatus status;

    protected Integer duration;

    private LocalDateTime startTime;

    public Task(String name, String description, TaskStatus status) {
        id = Identifier.INSTANCE.generate();
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
    }

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
    }

    public Task(int id, String name, String description, LocalDateTime startTime, Integer duration, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, LocalDateTime startTime, Integer duration, TaskStatus status) {
        this.id = Identifier.INSTANCE.generate();
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description) {
        id = Identifier.INSTANCE.generate();
        this.name = name;
        this.description = description;
    }

    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
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

    public LocalDateTime getEndTime() {
        return Objects.nonNull(startTime) ? startTime.plusMinutes(duration) : null;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name=" + name +
                ", description=" + description +
                ", taskType=" + EnumHelper.getTypeName(type) +
                ", status=" + EnumHelper.getStatusName(status) +
                ", duration=" + duration +
                ", endTime=" + getEndTime() +
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
