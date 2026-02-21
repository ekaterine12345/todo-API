package com.example.todoApp.services;

import com.example.todoApp.dtos.ApiResponse;
import com.example.todoApp.dtos.TaskDto;
import com.example.todoApp.entities.Task;
import com.example.todoApp.enums.TaskStatus;
import com.example.todoApp.mapper.TaskMapper;
import com.example.todoApp.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    // =================== Get task by Id ============================================================
    @Test
    void shouldGetTaskByIdSuccessfully(){
        // Given
        Long taskId = 1L;
        TaskDto taskDto = new TaskDto("Title", "Description ...", new Date());
        Task existingTask = new Task(taskDto);


        // When
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        ApiResponse response = taskService.getTaskById(taskId);
        // Then
        assertThat(response.getData()).isNotEmpty();
        assertTrue(response.getErrors().isEmpty());
        assertTrue(response.getData().containsKey("task"));
        assertThat(response.getData().get("task")).isEqualTo(existingTask);


        verify(taskRepository).findById(taskId);
    }

    @Test
    void shouldReturnErrorWhenGettingNonExistingTask(){
        // Given
        Long taskId = 1L;


        // When
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        ApiResponse response = taskService.getTaskById(taskId);

        // Then
        assertThat(response.getErrors()).isNotEmpty();
        assertTrue(response.getData().isEmpty());
        assertThat(response.getErrors()).containsEntry("id", "Task not found with id = " + taskId);

        verify(taskRepository).findById(taskId);

    }


    // =================== Add Task ============================================================
    @Test
    void shouldAddTaskSuccessfully(){
        // Given
        TaskDto taskDto = new TaskDto("Title", "Description ...", new Date());
        Task savedTask = new Task(taskDto);

        // When
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        ApiResponse response = taskService.addTask(taskDto);

        // Then
        assertThat(response.getData()).isNotEmpty();
        assertTrue(response.getErrors().isEmpty());
        assertTrue(response.getData().containsKey("New Task"));

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionWhenTaskDtoIsNull(){
        assertThrows(NullPointerException.class, () -> taskService.addTask(null));
    }


    // =================== delete task ============================================================
    @Test
    void shouldDeleteTaskSuccessfully(){
        // Given
        Long task_id = 1L;
        TaskDto taskDto = new TaskDto("Title", "Description ...", new Date());
        Task task = new Task(taskDto);

        // When
        when(taskRepository.findById(task_id)).thenReturn(Optional.of(task));
        ApiResponse response = taskService.deleteById(task_id);


        // Then
        assertThat(response.getData()).isNotEmpty();
        assertTrue(response.getErrors().isEmpty());
        assertTrue(response.getData().containsKey("deleted task"));

        verify(taskRepository, times(1)).deleteById(task_id);
        verify(taskRepository, never()).save(any()); // Verify that save was NEVER called during a delete operation
    }


    @Test
    void shouldReturnErrorWhenDeletingMissingTask(){
        // Given
        Long task_id = 1L;

        // When
        when(taskRepository.findById(task_id)).thenReturn(Optional.empty());
        ApiResponse response = taskService.deleteById(task_id);

        // Then
        assertTrue(response.getData().isEmpty());
        assertThat(response.getErrors()).isNotEmpty();
        assertTrue(response.getErrors().containsKey("id"));
        assertTrue(response.getErrors().containsValue("No task found with id = " + task_id));

        // verify db is not called
        verify(taskRepository, never()).deleteById(anyLong());
    }


    // =================== update task ============================================================
    @Test
    void shouldUpdateTaskSuccessfully(){


        // Given
        Long task_id = 1L;
        Task existingTask = new Task(new TaskDto("Title 1", "Description 1", new Date()));

        TaskDto taskDto = new TaskDto("Updated Title", "Updated Description ...", new Date());

        // When
        when(taskRepository.findById(task_id)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);
        ApiResponse response = taskService.updateTask(task_id, taskDto);

        // Then
        assertTrue(response.getErrors().isEmpty());
        assertThat(response.getData()).isNotEmpty();
        assertTrue(response.getData().containsKey("updated task"));

        verify(taskMapper).updateTaskFromDto(taskDto, existingTask);
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void shouldThrowExceptionWhenUpdateTaskDtoIsNull(){  /// is name okay?
        assertThrows(NullPointerException.class, () -> taskService.updateTask(1L,null));
    }

    @Test
    void shouldReturnErrorWhenUpdatingNonExistingTask(){   /// is name okay?
        // Given
        Long task_id = 1L;

        // When
        when(taskRepository.findById(task_id)).thenReturn(Optional.empty());

        ApiResponse response =
                taskService.updateTask(task_id, new TaskDto("title 1", null, null));

        // Then
        assertTrue(response.getData().isEmpty());
        assertThat(response.getErrors()).isNotEmpty();
        assertTrue(response.getErrors().containsKey("id"));
        assertTrue(response.getErrors().containsValue("No task found with id = " + task_id));
        //// verify ...

        verify(taskRepository, never()).save(any(Task.class));
    }


    // =================== patch task status ============================================================

    @Test
    void shouldUpdateStatusFromTodoToInProgress() {
        // success: To DO -> In Progress
        // Given
        Long id = 1L;
        Task task = new Task(new TaskDto("Title", "Desc", new Date()));
        task.setTaskStatus(TaskStatus.TODO);

        // When
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        ApiResponse response = taskService.updateTaskStatus(id, TaskStatus.IN_PROGRESS);

        // Then
        assertTrue(response.getErrors().isEmpty());
        assertThat(response.getData()).isNotEmpty();
        assertEquals(TaskStatus.IN_PROGRESS, task.getTaskStatus());

        verify(taskRepository).save(task);
    }

    @Test
    void shouldUpdateStatusFromInProgressToDone() {
        // success: In Progress -> Done

        // Given
        Long id = 1L;
        Task task = new Task(new TaskDto("Title", "Desc", new Date()));
        task.setTaskStatus(TaskStatus.IN_PROGRESS);

        // When
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        ApiResponse response = taskService.updateTaskStatus(id, TaskStatus.DONE);

        // Then
        assertTrue(response.getErrors().isEmpty());
        assertEquals(TaskStatus.DONE, task.getTaskStatus());

        verify(taskRepository).save(task);
    }


    @Test
    void shouldReturnErrorWhenStatusIsSame() {
        // negative: same -> same; here: to do ->  to do
        // Given
        Long id = 1L;
        TaskStatus currentStatus = TaskStatus.TODO;

        Task task = new Task(new TaskDto("Title", "Desc", new Date()));
        task.setTaskStatus(currentStatus);

        // When
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        ApiResponse response = taskService.updateTaskStatus(id, currentStatus);

        // Then
        assertTrue(response.getData().isEmpty());
        assertThat(response.getErrors()).containsKey("status");
        assertThat(response.getErrors()).containsValue("Task is already in status: "+currentStatus);

        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldReturnErrorWhenSkippingStatusFromTodoToDone() {
        // negative: To Do -> Done
        // Given
        Long id = 1L;

        Task task = new Task(new TaskDto("Title", "Desc", new Date()));
        task.setTaskStatus(TaskStatus.TODO);

        // When
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        ApiResponse response = taskService.updateTaskStatus(id, TaskStatus.DONE);

        // Then
        assertTrue(response.getData().isEmpty());
        assertThat(response.getErrors()).containsKey("status");

        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldReturnErrorWhenMovingBackwardsFromInProgressToTodo() {
        // negative: In progress -> To Do
        // Given
        Long id = 1L;
        Task task = new Task(new TaskDto("Title", "Desc", new Date()));
        task.setTaskStatus(TaskStatus.IN_PROGRESS);

        // When
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        ApiResponse response = taskService.updateTaskStatus(id, TaskStatus.TODO);

        // Then
        assertTrue(response.getData().isEmpty());
        assertThat(response.getErrors()).containsKey("status");
        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldReturnErrorWhenChangingStatusFromDone() {
        // negative: Done -> *; here Done -> In Progress
        // Given
        Long id = 1L;
        Task task = new Task(new TaskDto("Title", "Desc", new Date()));
        task.setTaskStatus(TaskStatus.DONE);

        // When
        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        ApiResponse response = taskService.updateTaskStatus(id, TaskStatus.IN_PROGRESS);

        // Then
        assertTrue(response.getData().isEmpty());
        assertThat(response.getErrors()).containsKey("status");
        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldReturnErrorWhenTaskNotFoundForStatusUpdate() {
        // Negative:  Task not found
        // Given
        Long id = 1L;

        // When
        when(taskRepository.findById(id)).thenReturn(Optional.empty());
        ApiResponse response = taskService.updateTaskStatus(id, TaskStatus.DONE);

        // Then
        assertTrue(response.getData().isEmpty());
        assertThat(response.getErrors()).containsKey("id");
        verify(taskRepository, never()).save(any());
    }




}