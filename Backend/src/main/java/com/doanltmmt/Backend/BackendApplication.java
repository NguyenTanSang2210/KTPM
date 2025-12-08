package com.doanltmmt.Backend;

import com.doanltmmt.Backend.entity.Role;
import com.doanltmmt.Backend.entity.User;
import com.doanltmmt.Backend.repository.RoleRepository;
import com.doanltmmt.Backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    // Tạo dữ liệu mẫu khi app khởi động
    @Bean
    CommandLineRunner initData(RoleRepository roleRepo, UserRepository userRepo) {
        return args -> {
            // 1. Tạo 3 quyền nếu chưa có
            Role adminRole = roleRepo.findByName("ADMIN")
                    .orElseGet(() -> roleRepo.save(new Role("ADMIN")));

            Role lecturerRole = roleRepo.findByName("LECTURER")
                    .orElseGet(() -> roleRepo.save(new Role("LECTURER")));

            Role studentRole = roleRepo.findByName("STUDENT")
                    .orElseGet(() -> roleRepo.save(new Role("STUDENT")));

            // 2. Tạo 1 tài khoản admin demo nếu chưa có
            if (userRepo.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("123456"); // tạm thời, sau sẽ mã hoá
                admin.setFullName("Admin Hệ Thống");
                admin.setEmail("admin@example.com");
                admin.setRole(adminRole);
                admin.setActive(true);
                userRepo.save(admin);
            }
        };
    }
}
