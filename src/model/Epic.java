package model;

import type.Status;
import type.Type;

import java.util.*;
import java.util.stream.Collectors;

public class Epic extends Task {
    private List<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.type = Type.EPIC;
        this.status = Status.NEW;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + Type.getTypeName(type) + '\'' +
                ", status='" + Status.getStatusName(status) + '\'' +
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
