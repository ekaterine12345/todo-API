package com.example.todoApp.repositories;


import com.example.todoApp.entities.Task;
import com.example.todoApp.enums.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("Should find task by specific status")
    void shouldFindTaskByStatus(){
        // Given
        Task task = new Task();
        final String task_title = "Test Task";

        task.setTitle(task_title);
        task.setDescription("Testing descrption....");
        task.setDeadline(new Date());
        task.setTaskStatus(TaskStatus.TODO);
        taskRepository.save(task);

        // When
        List<Task> tasks = taskRepository.findByTaskStatus(TaskStatus.TODO);
        List<Task> emptyTasks = taskRepository.findByTaskStatus(TaskStatus.DONE);


        // Then
        assertThat(tasks).hasSize(1);
        assertThat(tasks.get(0).getTitle()).isEqualTo(task_title);

        assertThat(emptyTasks).isEmpty();

    }


    @Test
    @DisplayName("Should default status to TODO when not specified")
    void shouldDefaultToTodo() {
        // Given
        Task task = new Task();
        task.setTitle("Mini Task");
        // status is not set manually

        // When
        Task saved = taskRepository.saveAndFlush(task); // to actually send request to DB (not cache)

        // Then
        assertThat(saved.getTaskStatus()).isNotNull();
        assertThat(saved.getTaskStatus()).isEqualTo(TaskStatus.TODO);
    }


}