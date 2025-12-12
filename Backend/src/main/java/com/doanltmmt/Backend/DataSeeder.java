package com.doanltmmt.Backend;

import com.doanltmmt.Backend.entity.Role;
import com.doanltmmt.Backend.entity.Student;
import com.doanltmmt.Backend.entity.User;
import com.doanltmmt.Backend.repository.RoleRepository;
import com.doanltmmt.Backend.repository.StudentRepository;
import com.doanltmmt.Backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final PasswordEncoder encoder;

    public DataSeeder(RoleRepository roleRepo, UserRepository userRepo, StudentRepository studentRepo, PasswordEncoder encoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.encoder = encoder;
    }

    @Transactional
    public void seed() {
        // 1. Ensure roles exist
        Role adminRole = roleRepo.findByName("ADMIN")
                .orElseGet(() -> roleRepo.save(new Role("ADMIN")));

        Role lecturerRole = roleRepo.findByName("LECTURER")
                .orElseGet(() -> roleRepo.save(new Role("LECTURER")));

        Role studentRole = roleRepo.findByName("STUDENT")
                .orElseGet(() -> roleRepo.save(new Role("STUDENT")));

        // 2. Seed admin demo or normalize password if not encrypted
        var adminOpt = userRepo.findByUsername("admin");
        if (adminOpt.isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("123456"));
            admin.setFullName("Admin Hệ Thống");
            admin.setEmail("admin@example.com");
            admin.setRole(adminRole);
            admin.setActive(true);
            userRepo.save(admin);
        } else {
            User admin = adminOpt.get();
            if (admin.getPassword() != null && !admin.getPassword().startsWith("$2")) {
                admin.setPassword(encoder.encode(admin.getPassword()));
                userRepo.save(admin);
            }
        }

        // 3. Seed student demo user
        User studentUser = userRepo.findByUsername("student").orElseGet(() -> {
            User st = new User();
            st.setUsername("student");
            st.setPassword(encoder.encode("123456"));
            st.setFullName("Sinh viên Demo");
            st.setEmail("student@example.com");
            st.setRole(studentRole);
            st.setActive(true);
            return userRepo.save(st);
        });

        // Seed Student entity mapped with @MapsId to the user (within same transaction)
        if (!studentRepo.existsById(studentUser.getId())) {
            Student s = new Student();
            // Use a managed reference in the same transactional context
            User managedUser = userRepo.getReferenceById(studentUser.getId());
            s.setUser(managedUser);
            s.setStudentCode("SV001");
            s.setClassName("LTM1");
            studentRepo.save(s);
        }
    }
}

