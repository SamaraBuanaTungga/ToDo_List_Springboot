package com.example.demo.repository;

import com.example.demo.model.GroupTodo;
import com.example.demo.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupTodoRepository extends JpaRepository<GroupTodo, Long> {
 @Query("SELECT pm.groupTodo FROM ProjectMember pm WHERE pm.user = :user")
List<GroupTodo> findProjectsByMember(@Param("user") User user);
List<GroupTodo> findByCreatedByAndTitleContainingIgnoreCase(User createdBy, String title);

    List<GroupTodo> findByCreatedBy(User user);
}

