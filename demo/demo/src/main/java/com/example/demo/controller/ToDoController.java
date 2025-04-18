package com.example.demo.controller;

import com.example.demo.model.ToDo;
import com.example.demo.repository.ToDoRepository;
import com.example.demo.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class ToDoController {

    private final ToDoRepository toDoRepository;

    @Autowired
    private ToDoService toDoService;

    ToDoController(ToDoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("todos", toDoService.getAllTodos());
        model.addAttribute("newTodo", new ToDo());
        return "index";
    }

    @PostMapping("/add")
    public String addTodo(@ModelAttribute ToDo todo) {
        toDoService.saveTodo(todo);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id) {
        toDoService.deleteTodo(id);
        return "redirect:/";
    }

    @PostMapping("/update/{id}")
    public String updateTodo(@PathVariable Long id) {
        Optional<ToDo> todo = toDoService.getTodoById(id);
        todo.ifPresent(t -> {
            t.setCompleted(!t.isCompleted());
            toDoService.saveTodo(t);
        });
        return "redirect:/";
    }

    @GetMapping("/filter")
    public String filterTodos(@RequestParam String filter, Model model) {
        List<ToDo> filteredTodos;
        switch (filter) {
            case "completed":
                filteredTodos = toDoRepository.findByCompleted(true);
                break;
            case "incomplete":
                filteredTodos = toDoRepository.findByCompleted(false);
                break;
            default:
                filteredTodos = toDoRepository.findAll();
        }
        model.addAttribute("todos", filteredTodos);
        model.addAttribute("filter", filter);
        return "index";
    }
}