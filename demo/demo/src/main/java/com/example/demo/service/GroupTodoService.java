package com.example.demo.service;

import com.example.demo.model.GroupTodo;
import com.example.demo.model.User;
import com.example.demo.repository.GroupTodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupTodoService {

    @Autowired
    private GroupTodoRepository groupTodoRepository;

    public List<GroupTodo> getAllProjects() {
        return groupTodoRepository.findAll();
    }

    public void addProject(String title, User user) {
        GroupTodo group = new GroupTodo();
        group.setTitle(title);
        group.setCreatedBy(user);
        groupTodoRepository.save(group);
    }

    public void deleteProject(Long id) {
        groupTodoRepository.deleteById(id);
    }

    public GroupTodo getProjectById(Long id) {
        return groupTodoRepository.findById(id).orElse(null);
    }
}