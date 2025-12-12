package com.doanltmmt.Backend;

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
    CommandLineRunner initData(DataSeeder seeder) {
        return args -> seeder.seed();
    }
}
