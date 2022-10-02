package model;

import type.TaskStatus;
import type.TaskType;
import utils.EnumHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subtasksIds = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.type = TaskType.EPIC;
        this.status = TaskStatus.NEW;
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.type = TaskType.EPIC;
        this.status = TaskStatus.NEW;
    }

    @Override
    public LocalDateTime getEndTime() {
        return Objects.nonNull(endTime) ? endTime : null;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name=" + name +
                ", description=" + description +
                ", type=" + EnumHelper.getTypeName(type) +
                ", status=" + EnumHelper.getStatusName(status) +
                ", subtasksIds.size()=" + (Objects.nonNull(subtasksIds) ? subtasksIds.size() : 0) +
                '}';
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(List<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }
}
