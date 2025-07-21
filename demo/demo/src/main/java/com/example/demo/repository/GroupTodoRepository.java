package com.example.demo.repository;

import com.example.demo.model.GroupTodo;
import com.example.demo.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupTodoRepository extends JpaRepository<GroupTodo, Long> {
    List<GroupTodo> findByCreatedBy(User user);
}

