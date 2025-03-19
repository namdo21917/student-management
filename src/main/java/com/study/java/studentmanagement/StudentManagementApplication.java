package com.study.java.studentmanagement;

import com.study.java.studentmanagement.swing.Login;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class StudentManagementApplication {

    public static void main(String[] args) {
        // Configure Spring Boot to not use headless mode
        System.setProperty("java.awt.headless", "false");

        // Start Spring Boot application
        ConfigurableApplicationContext context = new SpringApplicationBuilder(StudentManagementApplication.class)
                .headless(false)
                .run(args);

        // Launch Swing UI
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Create and show login window
                Login loginFrame = new Login();
                loginFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
