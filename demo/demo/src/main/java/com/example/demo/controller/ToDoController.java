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

import java.util.stream.Collectors;


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
        if (t.isCompleted()) {
            t.setCompleteAt(LocalDate.now());
        } else {
            t.setCompleteAt(null); // kalau dibatalkan selesai
        }
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
            filteredTodos = todoRepo.findByUserAndCompleted(user, true)
                    .stream()
                    .filter(todo -> todo.getGroup() == null)  // ⬅️ hanya tugas individu
                    .collect(Collectors.toList());
            break;
        case "incomplete":
            filteredTodos = todoRepo.findByUserAndCompleted(user, false)
                    .stream()
                    .filter(todo -> todo.getGroup() == null)  // ⬅️ hanya tugas individu
                    .collect(Collectors.toList());
            break;
        default:
            filteredTodos = todoRepo.findByUserAndGroupIsNull(user);  // sudah aman
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
                todo.setDeadline(LocalDate.parse(deadline));
            }
            toDoService.saveTodo(todo);
        }
        return "redirect:/";
    }

    @GetMapping("/task/{id}/complete")
public String markTaskAsCompleted(@PathVariable Long id) {
    Optional<ToDo> todo = toDoService.getTodoById(id);
    todo.ifPresent(t -> {
        t.setCompleted(true);
        t.setCompleteAt(LocalDate.now());
        toDoService.saveTodo(t);
    });
    return "redirect:/project/{id}";
}

    @PostMapping("/task/{id}/complete")
public String completeTask(@PathVariable Long id) {
    Optional<ToDo> todo = toDoService.getTodoById(id);
    todo.ifPresent(t -> {
        t.setCompleted(true);
        t.setCompleteAt(LocalDate.now());
        toDoService.saveTodo(t);
    });
    return "redirect:/project/{id}"; // ubah jika ingin redirect spesifik
}

@GetMapping("/search")
public String searchTodos(@RequestParam("keyword") String keyword, Model model, Principal principal) {
    if (principal != null) {
        User user = userRepo.findByUsername(principal.getName()).orElseThrow();

        List<ToDo> results = todoRepo.findByUserAndGroupIsNull(user).stream()
            .filter(todo -> {
                String task = todo.getTask() != null ? todo.getTask().toLowerCase() : "";
                String title = todo.getTitle() != null ? todo.getTitle().toLowerCase() : "";
                return task.contains(keyword.toLowerCase()) || title.contains(keyword.toLowerCase());
            })
            .collect(Collectors.toList());

        model.addAttribute("todos", results);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("searchKeyword", keyword);
    } else {
        model.addAttribute("todos", List.of());
    }

    return "index";
}



}
