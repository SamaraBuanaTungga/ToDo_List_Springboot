package com.example.demo.repository;

import com.example.demo.model.ProjectMember;
import com.example.demo.model.User;
import com.example.demo.model.GroupTodo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByUser(User user);
    List<ProjectMember> findByGroupTodo(GroupTodo groupTodo); // perbaikan nama
    boolean existsByUserAndGroupTodo(User user, GroupTodo groupTodo); // perbaikan nama
}
