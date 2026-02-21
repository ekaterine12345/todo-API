package com.example.todoApp.entities;

import com.example.todoApp.dtos.TaskDto;
import com.example.todoApp.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tasks")
public class Task extends BaseEntity<Long> {
    @Id
    @Column(name = "task_id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String description;
    private Date deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus taskStatus = TaskStatus.TODO; // default value it to do


    public Task(TaskDto taskDto) {
       this.setTitle(taskDto.getTitle());
       this.setDescription(taskDto.getDescription());
       this.setDeadline(taskDto.getDeadline());
       this.taskStatus = TaskStatus.TODO;
    }

    public TaskDto toDto(){
        return new TaskDto(title, description, deadline);
    }

    // 3. The "Safety Net" Lifecycle Hook
    @PrePersist
    protected void onCreate() {
        if (this.taskStatus == null) {
            this.taskStatus = TaskStatus.TODO;
        }
    }

}
