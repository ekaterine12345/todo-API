package com.example.todoApp.services;

import com.example.todoApp.dtos.ApiResponse;
import com.example.todoApp.dtos.TaskDto;
import com.example.todoApp.entities.Task;
import com.example.todoApp.enums.TaskStatus;
import com.example.todoApp.mapper.TaskMapper;
import com.example.todoApp.repositories.TaskRepository;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public ApiResponse getAllTasks() {
        return new ApiResponse("tasks", taskRepository.findAll());
    }

    public ApiResponse getTaskById(Long id){
        return taskRepository.findById(id)
                .map(task -> new ApiResponse("task", task ))
                .orElseGet(() -> new ApiResponse().addError("id", "Task not found with id = " + id));
    }

    public ApiResponse addTask (TaskDto taskDto){
        if (taskDto == null) {
            throw  new NullPointerException("Task should not be null");
        }

        Task task = new Task(taskDto);
        Task insertedTask = taskRepository.save(task);
        return new ApiResponse("New Task", insertedTask);
    }

    public ApiResponse updateTask(Long id, TaskDto taskDto){
        if (taskDto == null) {
            throw  new NullPointerException("Task should not be null");
        }

        return taskRepository.findById(id).map(task -> {
            taskMapper.updateTaskFromDto(taskDto, task);
            Task updatedTask = taskRepository.save(task);
            return new ApiResponse("updated task", updatedTask);
        }).orElseGet(() -> new ApiResponse().addError("id", "No task found with id = " + id ));

    }

    public ApiResponse deleteById(Long id){
        return taskRepository.findById(id).map(task -> {
            taskRepository.deleteById(id);
            return new ApiResponse("deleted task", task);
        }).orElseGet(() -> new ApiResponse().addError("id", "No task found with id = " + id ));
    }

    public ApiResponse updateTaskStatus(Long id, TaskStatus newStatus){
        // should work: TO DO -> In Progress -> Done
        return taskRepository.findById(id).map(task -> {

            TaskStatus currentStatus = task.getTaskStatus();

            if (currentStatus ==  newStatus) {    // issue: same status
                return  new ApiResponse().addError("status", "Task is already in status: "+currentStatus);
            }

            if (!currentStatus.canTransitionTo(newStatus)) {
                return new ApiResponse().addError("status",
                        "Invalid status transition from " + currentStatus + " to " + newStatus);
            }

            task.setTaskStatus(newStatus);
            Task updatedTask = taskRepository.save(task);
            return new ApiResponse("Task status updated successfully", updatedTask);
        }).orElseGet(() -> new ApiResponse().addError("id", "Task not found with id: " + id));
    }
    public ApiResponse getTasksByStatus(TaskStatus taskStatus) {
        return  new ApiResponse("Tasks with status: " + taskStatus,
                taskRepository.findByTaskStatus(taskStatus));
    }
}
