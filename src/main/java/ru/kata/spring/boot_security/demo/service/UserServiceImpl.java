package ru.kata.spring.boot_security.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;

    public User findUsersById(Long id) {
        return userRepository.getOne(id);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }


    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = userRepository.findByUsername(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("User with this " + username + " User Name not found");
        }
        return userDetails;
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public boolean saveUser(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            return false;
        }
        if (user.getUsername().equals("") | user.getPassword().equals("")) {
            return false;
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean updateUser(User user) {
        if (user.getUsername().equals("") | user.getPassword().equals("")) {
            return false;
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    public Long getUsernameByName(String name) {
        User user = userRepository.findByUsername(name);
        return user.getId();
    }
    public boolean saveUserTest(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            return false;
        }
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public User getUserForUpdateUsers(User user, String username) {
        User userDb = findUserById(getUsernameByName(username));
        Set<Role> roles = userDb.getRoles();
        user.setRoles(roleService.getRoleByName(roles.stream().map(role-> role.getName()).toArray(String[]::new)));
        return user;
    }

    public User getUserAndRoles(User user, String[] roles) {
        if (roles == null) {
            user.setRoles(roleService.getRoleByName(new String[]{"ROLE_USER"}));
        } else {
            user.setRoles(roleService.getRoleByName(roles));
        }
        return user;
    }

    @Override
    public User getNotNullRole(User user) {
        if (user.getRoles() == null) {
            user.setRoles(Collections.singleton(new Role(2L)));
        }
        return user;
    }
}
