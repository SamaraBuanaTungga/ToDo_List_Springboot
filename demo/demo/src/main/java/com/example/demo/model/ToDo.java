package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "todos")
public class ToDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String task;

    private boolean completed;

    // Siapa yang membuat task ini
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    // Untuk task proyek
    @ManyToOne
    @JoinColumn(name = "group_id")
    private GroupTodo group;

    // Siapa yang dikerahkan (assigned user)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Deadline tugas
    private LocalDate deadline;

    // === Constructors ===
    public ToDo() {}

    public ToDo(String task, boolean completed) {
        this.task = task;
        this.completed = completed;
    }

    // === Getters & Setters ===
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public GroupTodo getGroup() {
        return group;
    }

    public void setGroup(GroupTodo group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline.now();
    }
}
