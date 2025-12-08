package com.doanltmmt.Backend.controller;

import com.doanltmmt.Backend.entity.Role;
import com.doanltmmt.Backend.entity.User;
import com.doanltmmt.Backend.repository.RoleRepository;
import com.doanltmmt.Backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173") // cho React dev sau này
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserController(UserRepository userRepository,
                          RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "Backend OK";
    }

    // tạo nhanh 1 admin demo + role để test DB
    @PostMapping("/init-admin")
    public User createAdminDemo() {
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role r = new Role("ADMIN");
                    r.setDescription("Quản trị hệ thống");
                    return roleRepository.save(r);
                });

        User u = new User();
        u.setUsername("admin");
        u.setPassword("123456"); // sau sẽ mã hoá
        u.setFullName("Admin Demo");
        u.setEmail("admin@example.com");
        u.setRole(adminRole);
        u.setActive(true);

        return userRepository.save(u);
    }

    @GetMapping
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
