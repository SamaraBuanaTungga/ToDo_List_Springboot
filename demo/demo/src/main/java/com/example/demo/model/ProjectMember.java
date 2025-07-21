package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "project_member")
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Join ke GroupTodo (project)
    @ManyToOne
    @JoinColumn(name = "group_id") // ini harus cocok dengan nama kolom foreign key di tabel
    private GroupTodo groupTodo;

    // Join ke User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // GETTER & SETTER
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GroupTodo getGroupTodo() {
        return groupTodo;
    }

    public void setGroupTodo(GroupTodo groupTodo) {
        this.groupTodo = groupTodo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
