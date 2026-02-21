package com.example.todoApp.controllers;

import com.example.todoApp.dtos.ApiResponse;
import com.example.todoApp.dtos.TaskDto;
import com.example.todoApp.enums.TaskStatus;
import com.example.todoApp.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ApiResponse getAllTasks(){
       return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ApiResponse getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @PostMapping
    public ApiResponse addTask(@RequestBody TaskDto taskDto){
        return taskService.addTask(taskDto);
    }

    @PutMapping("/{id}")
    public ApiResponse updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto){
        return taskService.updateTask(id, taskDto);
    }


    @DeleteMapping("/{id}")
    public ApiResponse deleteTask(@PathVariable Long id){
        return taskService.deleteById(id);
    }

    @PatchMapping("/{id}")
    public ApiResponse updateTaskStatus(@PathVariable Long id, @RequestParam TaskStatus taskStatus){
        return taskService.updateTaskStatus(id, taskStatus);
    }


    @GetMapping("/filter")
    public ApiResponse getTasksByStatus(@RequestParam TaskStatus taskStatus){
        return taskService.getTasksByStatus(taskStatus);
    }


    // ToDo:  patch request (update status);  filter by status
}
