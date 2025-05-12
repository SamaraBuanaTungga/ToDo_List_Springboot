package com.example.demo.controller;

import com.example.demo.model.ToDo;
import com.example.demo.repository.ToDoRepository;
import com.example.demo.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import java.security.Principal;
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
    public String home(Model model, Principal principal) {
        if (principal != null) {
            User user = userRepo.findByUsername(principal.getName()).orElseThrow();
            model.addAttribute("todos", todoRepo.findByUser(user));
            model.addAttribute("username", principal.getName());
        }
        model.addAttribute("newTodo", new ToDo());
        return "index";
    }

    @PostMapping("/add")
    public String addTodo(@ModelAttribute ToDo todo, Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
        todo.setUser(user);
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
    public String filterTodos(@RequestParam String filter, Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
        List<ToDo> filteredTodos;

        switch (filter) {
            case "completed":
                filteredTodos = toDoRepository.findByUserAndCompleted(user, true);
                break;
            case "incomplete":
                filteredTodos = toDoRepository.findByUserAndCompleted(user, false);
                break;
            default:
                filteredTodos = toDoRepository.findByUser(user);
        }

        model.addAttribute("todos", filteredTodos);
        model.addAttribute("filter", filter);
        model.addAttribute("username", principal.getName());
        return "index";
    }

    
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ToDoRepository todoRepo;

    @GetMapping("/todos")
    public String listTodos(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("todos", todoRepo.findByUser(user));
        model.addAttribute("username", principal.getName());
        return "index";
    }

    @PostMapping("/todos/create")
    public String createTodo(@ModelAttribute ToDo todo, Principal principal) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
        todo.setUser(user);
        todoRepo.save(todo);
        return "redirect:/index";

    }
    @PostMapping("/edit/{id}")
    public String editTodo(@PathVariable Long id, @RequestParam String task) {
        Optional<ToDo> optionalTodo = toDoService.getTodoById(id);
        if (optionalTodo.isPresent()) {
            ToDo todo = optionalTodo.get();
            todo.setTask(task);
            toDoService.saveTodo(todo);
        }
        return "redirect:/";
    }
    

}