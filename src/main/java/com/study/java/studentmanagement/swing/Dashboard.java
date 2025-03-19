package com.study.java.studentmanagement.swing;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Slf4j
public class Dashboard extends JFrame {
    private JPanel contentPane;
    private JPanel menuPanel;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Menu buttons
    private JButton btnHome;
    private JButton btnStudent;
    private JButton btnTeacher;
    private JButton btnCourse;
    private JButton btnGrade;
    private JButton btnMajor;
    private JButton btnSemester;
    private JButton btnTranscript;
    private JButton btnLogout;

    public Dashboard() {
        initComponents();
        setupListeners();
    }

    private void initComponents() {
        setTitle("Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 700);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        // Menu Panel
        menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(200, 0));
        menuPanel.setBackground(new Color(51, 51, 51));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        contentPane.add(menuPanel, BorderLayout.WEST);

        // Add logo or system name
        JLabel lblLogo = new JLabel("Student Management");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Arial", Font.BOLD, 16));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(new EmptyBorder(20, 0, 20, 0));
        menuPanel.add(lblLogo);

        // Create menu buttons
        btnHome = createMenuButton("Home");
        btnStudent = createMenuButton("Students");
        btnTeacher = createMenuButton("Teachers");
        btnCourse = createMenuButton("Courses");
        btnGrade = createMenuButton("Grades");
        btnMajor = createMenuButton("Majors");
        btnSemester = createMenuButton("Semesters");
        btnTranscript = createMenuButton("Transcripts");
        btnLogout = createMenuButton("Logout");

        // Add buttons to menu
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(btnHome);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnStudent);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnTeacher);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnCourse);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnGrade);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnMajor);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnSemester);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(btnTranscript);
        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(btnLogout);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Main content panel with CardLayout
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        contentPane.add(mainPanel, BorderLayout.CENTER);

        // Add panels for each section
        mainPanel.add(createHomePanel(), "HOME");
        mainPanel.add(createPlaceholderPanel("Students"), "STUDENTS");
        mainPanel.add(createPlaceholderPanel("Teachers"), "TEACHERS");
        mainPanel.add(createPlaceholderPanel("Courses"), "COURSES");
        mainPanel.add(createPlaceholderPanel("Grades"), "GRADES");
        mainPanel.add(createPlaceholderPanel("Majors"), "MAJORS");
        mainPanel.add(createPlaceholderPanel("Semesters"), "SEMESTERS");
        mainPanel.add(createPlaceholderPanel("Transcripts"), "TRANSCRIPTS");

        // Show home panel by default
        cardLayout.show(mainPanel, "HOME");
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 51, 51));
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(64, 64, 64));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(51, 51, 51));
            }
        });

        return button;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to Student Management System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        // Statistics panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 20, 20));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Add statistic cards
        statsPanel.add(createStatCard("Total Students", "0"));
        statsPanel.add(createStatCard("Total Teachers", "0"));
        statsPanel.add(createStatCard("Total Courses", "0"));
        statsPanel.add(createStatCard("Total Majors", "0"));
        statsPanel.add(createStatCard("Active Semester", "2023-2024"));
        statsPanel.add(createStatCard("Total Grades", "0"));
        statsPanel.add(createStatCard("Average Score", "0.0"));
        statsPanel.add(createStatCard("Pass Rate", "0%"));

        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(valueLabel);

        return card;
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(title + " Management");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(new EmptyBorder(20, 0, 20, 0));

        panel.add(label, BorderLayout.NORTH);
        return panel;
    }

    private void setupListeners() {
        btnHome.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));
        btnStudent.addActionListener(e -> cardLayout.show(mainPanel, "STUDENTS"));
        btnTeacher.addActionListener(e -> cardLayout.show(mainPanel, "TEACHERS"));
        btnCourse.addActionListener(e -> cardLayout.show(mainPanel, "COURSES"));
        btnGrade.addActionListener(e -> cardLayout.show(mainPanel, "GRADES"));
        btnMajor.addActionListener(e -> cardLayout.show(mainPanel, "MAJORS"));
        btnSemester.addActionListener(e -> cardLayout.show(mainPanel, "SEMESTERS"));
        btnTranscript.addActionListener(e -> cardLayout.show(mainPanel, "TRANSCRIPTS"));

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new Login().setVisible(true);
            }
        });
    }
}