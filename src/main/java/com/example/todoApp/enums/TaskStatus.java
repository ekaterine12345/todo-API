package com.example.todoApp.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {
    TODO("To Do"){
        @Override
        public boolean canTransitionTo(TaskStatus newStatus) {
            return newStatus == IN_PROGRESS;
        }
    },
    IN_PROGRESS("In Progress"){
        @Override
        public boolean canTransitionTo(TaskStatus newStatus) {
            return newStatus == DONE;
        }
    },
    DONE("Done"){
        @Override
        public boolean canTransitionTo(TaskStatus newStatus) {
            return false;
        }
    };

    private final String displayValue;

    public abstract boolean canTransitionTo(TaskStatus newStatus);
    TaskStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    @JsonValue
    public String getDisplayValue() {
        return displayValue;
    }

    @Override
    public String toString() {
        return displayValue;
    }
}
