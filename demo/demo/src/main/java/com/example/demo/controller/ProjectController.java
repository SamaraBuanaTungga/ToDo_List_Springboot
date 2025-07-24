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
import com.example.demo.model.ToDo;


import java.security.Principal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ProjectController {

    private final ToDoController toDoController;

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
private UserRepository userRepo;


    ProjectController(ToDoController toDoController) {
        this.toDoController = toDoController;
    }


    // HANYA SATU MAPPING UNTUK GET /project
    @GetMapping("/project")
public String showProjects(Model model, Principal principal) {
    if (principal != null) {
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Proyek yang dibuat oleh user
        List<GroupTodo> ownedProjects = groupTodoRepository.findByCreatedBy(currentUser);

        // Proyek yang user menjadi member (tidak termasuk yang dibuat sendiri)
        List<GroupTodo> memberProjects = groupTodoRepository.findProjectsByMember(currentUser);

        // Gabungkan kedua list tanpa duplikat (misalnya pakai Set)
        Set<GroupTodo> allProjects = new HashSet<>();
        allProjects.addAll(ownedProjects);
        allProjects.addAll(memberProjects);

        model.addAttribute("projects", allProjects);
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
@GetMapping("/project/{projectId}")
public String viewProjectDetail(@PathVariable Long projectId, Model model, Principal principal) {
    String currentUsername = principal.getName();
    Optional<User> currentUserOpt = userRepo.findByUsername(currentUsername);
    if (currentUserOpt.isEmpty()) {
        return "redirect:/login";
    }

    User currentUser = currentUserOpt.get();
    GroupTodo project = groupTodoRepository.findById(projectId).orElse(null);
    if (project == null) {
        return "redirect:/project";
    }

    boolean isOwner = project.getCreatedBy().getUsername().equals(currentUsername);
    boolean isMember = projectMemberRepository.existsByUserAndGroupTodo(currentUser, project);

    if (!isOwner && !isMember) {
        return "redirect:/project";
    }

    model.addAttribute("projectDetail", project);
    model.addAttribute("username", currentUsername);

    List<ToDo> allTasks = todoRepo.findByGroup(project);

    if (!isOwner) {
        allTasks = allTasks.stream()
                .filter(t -> t.getUser() != null && t.getUser().getUsername().equals(currentUsername))
                .collect(Collectors.toList());
    }

    LocalDate now = LocalDate.now();

    long totalCompletedOnTime = allTasks.stream()
            .filter(t -> {
                LocalDate deadline = t.getDeadline();
                LocalDate completedAt = t.getCompleteAt();
                return t.isCompleted()
                        && deadline != null
                        && completedAt != null
                        && !completedAt.isAfter(deadline);
            })
            .count();

    long totalLate = allTasks.stream()
            .filter(t -> {
                LocalDate deadline = t.getDeadline();
                LocalDate completedAt = t.getCompleteAt();

                if (deadline == null) return false;

                // Belum selesai tapi deadline lewat
                if (!t.isCompleted() && deadline.isBefore(now)) return true;

                // Selesai tapi melebihi deadline
                if (t.isCompleted() && completedAt != null && completedAt.isAfter(deadline)) return true;

                return false;
            })
            .count();

    long totalUnfinished = allTasks.stream()
            .filter(t -> !t.isCompleted()
                    && (t.getDeadline() == null || !t.getDeadline().isBefore(now)))
            .count();

    long totalTask = totalCompletedOnTime + totalLate + totalUnfinished;
    if (totalTask == 0) {
        totalTask = 1; // Hindari divide by zero
    }

    model.addAttribute("projectTasks", allTasks);
    model.addAttribute("isOwner", isOwner);
    model.addAttribute("totalCompletedTask", totalCompletedOnTime);
    model.addAttribute("totalLateTask", totalLate);
    model.addAttribute("totalUnfinishedTask", totalUnfinished);
    model.addAttribute("totalTaskCount", totalTask);

    return "project";
}



@PostMapping("/project/{id}/add-member")
public String addMemberToProject(@PathVariable Long id,
                                 @RequestParam String username,
                                 RedirectAttributes redirectAttributes) {
    GroupTodo project = groupTodoService.getProjectById(id);
    Optional<User> userOpt = userRepository.findByUsername(username);

    if (userOpt.isEmpty()) {
        redirectAttributes.addFlashAttribute("errorMessage", "User '" + username + "' tidak ditemukan.");
        return "redirect:/project/" + id;
    }

    User user = userOpt.get();

    // ❌ Cek apakah user yang ditambahkan adalah owner
    if (project.getCreatedBy().getId().equals(user.getId())) {
        redirectAttributes.addFlashAttribute("errorMessage", "Owner tidak bisa ditambahkan sebagai member.");
        return "redirect:/project/" + id;
    }

    // ❌ Cek apakah user sudah jadi member
    if (projectMemberRepository.existsByUserAndGroupTodo(user, project)) {
        redirectAttributes.addFlashAttribute("errorMessage", "User '" + username + "' sudah menjadi member.");
        return "redirect:/project/" + id;
    }

    // ✅ Tambahkan member baru
    ProjectMember member = new ProjectMember();
    member.setGroupTodo(project);
    member.setUser(user);
    projectMemberRepository.save(member);

    redirectAttributes.addFlashAttribute("successMessage", "Berhasil menambahkan member: " + username);
    return "redirect:/project/" + id;
}



@PostMapping("/project/{id}/add-task")
public String addTaskToProject(
        @PathVariable("id") Long projectId,
        @RequestParam("task") String task,
        @RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
        @RequestParam("assigneeId") Long assigneeId) {

    GroupTodo group = groupTodoRepository.findById(projectId).orElseThrow();
    User assignee = userRepo.findById(assigneeId).orElseThrow();

    ToDo newTask = new ToDo();
    newTask.setTask(task);
    newTask.setDeadline(deadline); // ✅ pakai deadline dari form
    newTask.setUser(assignee);
    newTask.setGroup(group);
    newTask.setCompleted(false);

    todoRepo.save(newTask);

    return "redirect:/project/" + projectId;
}



@PostMapping("/project/{projectId}/task/edit/{taskId}")
public String editProjectTask(@PathVariable Long projectId,
                              @PathVariable Long taskId,
                              @RequestParam String taskName,
                              Principal principal) {
    ToDo task = todoRepo.findById(taskId).orElseThrow();
    User currentUser = userRepo.findByUsername(principal.getName()).orElseThrow();

    if (task.getGroup() == null || !task.getGroup().getCreatedBy().getId().equals(currentUser.getId())) {
        return "redirect:/access-denied";
    }

    task.setTask(taskName);
    todoRepo.save(task);

    return "redirect:/project/" + projectId;
}


@PostMapping("/task/delete/{id}")
public String deleteTask(@PathVariable Long id, Principal principal) {
    ToDo task = todoRepo.findById(id).orElseThrow();
    User currentUser = userRepo.findByUsername(principal.getName()).orElseThrow();

    if (task.getGroup() == null || !task.getGroup().getCreatedBy().getId().equals(currentUser.getId())) {
        return "redirect:/access-denied";
    }

    Long projectId = task.getGroup().getId();
    todoRepo.delete(task);
    return "redirect:/project/" + projectId;
}


    @PostMapping("/task/{id}/update")
public String updateTask(@PathVariable Long id,
                         @RequestParam("task") String taskName,
                         Principal principal) {
    // Validasi: pastikan user adalah owner task
    ToDo task = todoRepo.findById(id).orElse(null);
    if (task == null) return "redirect:/project";

    String loggedInUsername = principal.getName();
    if (!task.getGroup().getCreatedBy().getUsername().equals(loggedInUsername)) {
        return "redirect:/project";
    }

    // Update nama task
    task.setTask(taskName);
    todoRepo.save(task);

    return "redirect:/project/" + task.getGroup().getId();
}

@PostMapping("/project/{projectId}/task/complete/{taskId}")
public String markTaskAsCompleted(@PathVariable Long projectId,
                                  @PathVariable Long taskId,
                                  Principal principal) {
    ToDo task = todoRepo.findById(taskId).orElse(null);
    if (task == null) return "redirect:/project/" + projectId;

    String username = principal.getName();
    User currentUser = userRepo.findByUsername(username).orElse(null);
    if (currentUser == null) return "redirect:/login";

    // ❌ Hapus validasi owner → ✅ Hanya user yang ditugaskan yang boleh menyelesaikan
    if (!task.getUser().getId().equals(currentUser.getId())) {
        return "redirect:/eror";
    }

    task.setCompleted(true);
    task.setCompleteAt(LocalDate.now());
    todoRepo.save(task);

    return "redirect:/project/" + projectId;
}


}