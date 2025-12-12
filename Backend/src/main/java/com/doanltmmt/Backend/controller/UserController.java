package com.doanltmmt.Backend.controller;

import com.doanltmmt.Backend.entity.Role;
import com.doanltmmt.Backend.entity.Student;
import com.doanltmmt.Backend.entity.User;
import com.doanltmmt.Backend.repository.StudentRepository;
import com.doanltmmt.Backend.repository.RoleRepository;
import com.doanltmmt.Backend.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173") // cho React dev sau này
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          StudentRepository studentRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public String test() {
        return "Backend OK";
    }

    // tạo nhanh 1 admin demo + role để test DB
    @PostMapping("/init-admin")
    @PreAuthorize("hasRole('ADMIN')")
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

    // tạo nhanh 1 giảng viên demo nếu chưa có
    @PostMapping("/init-lecturer")
    @PreAuthorize("hasRole('ADMIN')")
    public User createLecturerDemo() {
        Role lecturerRole = roleRepository.findByName("LECTURER")
                .orElseGet(() -> roleRepository.save(new Role("LECTURER")));

        return userRepository.findByUsername("lecturer")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("lecturer");
                    u.setPassword(passwordEncoder.encode("123456"));
                    u.setFullName("Giảng viên Demo");
                    u.setEmail("lecturer@example.com");
                    u.setRole(lecturerRole);
                    u.setActive(true);
                    return userRepository.save(u);
                });
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAll() {
        return userRepository.findAll();
    }

    // tạo nhanh 1 sinh viên demo nếu chưa có
    @PostMapping("/init-student")
    @PreAuthorize("hasRole('ADMIN')")
    public User createStudentDemo() {
        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseGet(() -> roleRepository.save(new Role("STUDENT")));

        User user = userRepository.findByUsername("student")
                .orElseGet(() -> {
                    User u = new User();
                    u.setUsername("student");
                    u.setPassword(passwordEncoder.encode("123456"));
                    u.setFullName("Sinh viên Demo");
                    u.setEmail("student@example.com");
                    u.setRole(studentRole);
                    u.setActive(true);
                    return userRepository.save(u);
                });

        // đảm bảo có bản ghi Student ánh xạ 1-1 với User
        if (!studentRepository.existsById(user.getId())) {
            Student s = new Student();
            s.setUser(user);
            s.setStudentCode("SV001");
            s.setClassName("LTM1");
            studentRepository.save(s);
        }

        return user;
    }
}
