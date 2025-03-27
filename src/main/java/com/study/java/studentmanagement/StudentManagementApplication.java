package com.study.java.studentmanagement;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.swing.*;

@SpringBootApplication
public class StudentManagementApplication {

    public static void main(String[] args) {
        // Configure Spring Boot to not use headless mode
        System.setProperty("java.awt.headless", "false");

        ConfigurableApplicationContext context = new SpringApplicationBuilder(StudentManagementApplication.class)
                .headless(false)
                .run(args);
    }
}

@Component
class UIStarter {
    private final ConfigurableApplicationContext context;

    public UIStarter(ConfigurableApplicationContext context) {
        this.context = context;
    }

    @EventListener
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        // Launch Swing UI after server is initialized
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Create and show login window

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
