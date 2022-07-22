package ru.kata.spring.boot_security.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;
    private final RoleRepository roleRepository;

    @PostConstruct
    public void addTestUsers() {
        roleRepository.save(new Role(1L, "ROLE_ADMIN"));
        roleRepository.save(new Role(2L, "ROLE_USER"));
        User newAdmin = new User("admin", "admin", roleService.getRoleByName(new String[]{"ROLE_ADMIN"}));
        userService.saveUserTest(newAdmin);
        User newUser = new User("user", "user", roleService.getRoleByName(new String[]{"ROLE_USER"}));
        userService.saveUserTest(newUser);
    }

    @GetMapping("/admin")
    public String showAllUsers(Model model) {
        List<User> user = userService.findAllUsers();
        model.addAttribute("allUser", user);
        return "admin";
    }

    @GetMapping("/admin/user-save")
    public String saveUserForm(Model model) {
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("user", new User());
        return "admin-save";
    }

    @PostMapping("/admin/user-save")
    public String saveUser(User user) {
        userService.getNotNullRole(user);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @DeleteMapping("/admin/user-delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }

    @GetMapping("/admin/user-update/{id}")
    public String updateUserForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("user", userService.findUserById(id));
        return "admin-update";
    }

    @PostMapping("/admin/user-update")
    public String updateUsers(@ModelAttribute("user") User user, @RequestParam(value = "nameRoles", required = false) String[] roles) {
        userService.getUserAndRoles(user, roles);
        userService.updateUser(user);
        return "redirect:/admin";
    }
}
