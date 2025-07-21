package com.example.demo.repository;

import com.example.demo.model.User;
import com.example.demo.model.GroupTodo;
import com.example.demo.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    List<ToDo> findByCompleted(boolean completed);
    List<ToDo> findByUser(User user);
    List<ToDo> findByGroup(GroupTodo group);
    List<ToDo> findByUserAndCompleted(User user, boolean completed);
    
}
