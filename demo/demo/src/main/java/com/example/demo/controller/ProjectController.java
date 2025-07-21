package com.example.demo.controller;

import com.example.demo.model.GroupTodo;
import com.example.demo.model.ProjectMember;
import com.example.demo.model.ToDo;
import com.example.demo.model.User;
import com.example.demo.repository.GroupTodoRepository;
import com.example.demo.repository.ProjectMemberRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProjectMemberRepository;
import com.example.demo.service.GroupTodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class ProjectController {

    @Autowired
    private GroupTodoService groupTodoService;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupTodoRepository groupTodoRepository;

    @Autowired
private com.example.demo.repository.ToDoRepository todoRepo;

@Autowired
private com.example.demo.repository.UserRepository userRepo;


    // HANYA SATU MAPPING UNTUK GET /project
    @GetMapping("/project")
    public String showProjects(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            List<GroupTodo> myProjects = groupTodoRepository.findByCreatedBy(currentUser);
            model.addAttribute("projects", myProjects);
            model.addAttribute("username", username);
        } else {
            model.addAttribute("projects", List.of());
        }
        return "project";
    }

    @PostMapping("/project/add")
    public String addProject(@RequestParam String title, Principal principal) {
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        groupTodoService.addProject(title, currentUser);
        return "redirect:/project";
    }

    @PostMapping("/project/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        groupTodoService.deleteProject(id);
        return "redirect:/project";
    }

   @GetMapping("/project/{id}")
public String viewProjectDetail(@PathVariable Long id, Model model, Principal principal) {
    GroupTodo project = groupTodoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Project not found"));

    model.addAttribute("projectDetail", project);
    model.addAttribute("username", principal.getName());

    return "project";
}


@PostMapping("/project/{id}/add-member")
public String addMemberToProject(@PathVariable Long id, @RequestParam String username) {
    GroupTodo project = groupTodoService.getProjectById(id);
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User tidak ditemukan: " + username));

    ProjectMember member = new ProjectMember();
    member.setGroupTodo(project);
    member.setUser(user);
    projectMemberRepository.save(member);

    return "redirect:/project/" + id;
}

@PostMapping("/project/{id}/add-task")
public String addTaskToProject(
        @PathVariable("id") Long projectId,
        @RequestParam("task") String task,
        @RequestParam("deadline") String deadline,
        @RequestParam("assigneeId") Long assigneeId,
        Principal principal) {

    GroupTodo group = groupTodoRepository.findById(projectId)
            .orElseThrow(() -> new RuntimeException("Group not found"));

    User assignee = userRepository.findById(assigneeId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    User creator = userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

    ToDo newTask = new ToDo();
    newTask.setTask(task);
    newTask.setDeadline(LocalDate.parse(deadline));
    newTask.setUser(assignee);
    newTask.setGroup(group);
    newTask.setCompleted(false);
    newTask.setCreatedBy(creator);

    todoRepo.save(newTask);

    return "redirect:/project/" + projectId;
}



}