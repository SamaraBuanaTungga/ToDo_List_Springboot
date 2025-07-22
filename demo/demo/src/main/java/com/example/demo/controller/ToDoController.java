package com.example.demo.controller;

import com.example.demo.model.ToDo;
import com.example.demo.model.User;
import com.example.demo.repository.ToDoRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class ToDoController {

    @Autowired private ToDoService toDoService;
    @Autowired private UserRepository userRepo;
    @Autowired private ToDoRepository todoRepo;

    @GetMapping
public String home(Model model, Principal principal) {
    if (principal != null) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();
        model.addAttribute("todos", todoRepo.findByUserAndGroupIsNull(user));
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
                filteredTodos = todoRepo.findByUserAndCompleted(user, true);
                break;
            case "incomplete":
                filteredTodos = todoRepo.findByUserAndCompleted(user, false);
                break;
            default:
    filteredTodos = todoRepo.findByUserAndGroupIsNull(user);

        }

        model.addAttribute("todos", filteredTodos);
        model.addAttribute("filter", filter);
        model.addAttribute("username", principal.getName());
        return "index";
    }

  @GetMapping("/todos")
public String listTodos(Model model, Principal principal) {
    User user = userRepo.findByUsername(principal.getName()).orElseThrow();
    model.addAttribute("todos", todoRepo.findByUserAndGroupIsNull(user));
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
    public String editTodo(@PathVariable Long id,
                           @RequestParam String task,
                           @RequestParam(required = false) String deadline) {
        Optional<ToDo> optionalTodo = toDoService.getTodoById(id);
        if (optionalTodo.isPresent()) {
            ToDo todo = optionalTodo.get();
            todo.setTask(task);
            if (deadline != null && !deadline.isEmpty()) {
                todo.setDeadline(LocalDate.now());
            }
            toDoService.saveTodo(todo);
        }
        return "redirect:/";
    }

    @GetMapping("/task/{id}/complete")
    public String markTaskAsCompleted(@PathVariable Long id) {
    Optional<ToDo> todo = toDoService.getTodoById(id);
    todo.ifPresent(t -> {
        t.setCompleted(true); // atau toggle jika kamu ingin ubah-ubah statusnya
        toDoService.saveTodo(t);
    });
    return "redirect:/project/{id}";
}
    @PostMapping("/task/{id}/complete")
public String completeTask(@PathVariable Long id) {
    Optional<ToDo> todo = toDoService.getTodoById(id);
    todo.ifPresent(t -> {
        t.setCompleted(true);
        toDoService.saveTodo(t);
    });
    return "redirect:/project/{id}"; // atau redirect ke halaman proyek
}

}
