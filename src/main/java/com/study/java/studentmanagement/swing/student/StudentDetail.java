package com.study.java.studentmanagement.swing.student;

import com.formdev.flatlaf.FlatClientProperties;
import com.study.java.studentmanagement.model.Major;
import com.study.java.studentmanagement.model.Teacher;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.MajorRepository;
import com.study.java.studentmanagement.repository.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

@Slf4j
@Component
public class StudentDetail extends JDialog {
    private final TeacherRepository teacherRepository;
    private final MajorRepository majorRepository;

    public StudentDetail(JFrame parent, User user, TeacherRepository teacherRepository,
            MajorRepository majorRepository) {
        super(parent, "Chi tiết sinh viên", true);
        this.teacherRepository = teacherRepository;
        this.majorRepository = majorRepository;

        setSize(500, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initializeUI(user);
    }

    private void initializeUI(User user) {
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().setLayout(new BorderLayout());

        // Create content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add student information
        addLabelAndValue(contentPanel, "Tên:", user.getFullName(), gbc, 0);
        addSeparator(contentPanel, gbc, 1);
        addLabelAndValue(contentPanel, "Mã sinh viên:", user.getMsv(), gbc, 2);
        addSeparator(contentPanel, gbc, 3);
        addLabelAndValue(contentPanel, "Năm:", user.getYear(), gbc, 4);
        addSeparator(contentPanel, gbc, 5);
        addLabelAndValue(contentPanel, "Giới tính:", user.getGender(), gbc, 6);
        addSeparator(contentPanel, gbc, 7);
        addLabelAndValue(contentPanel, "Lớp:", user.getClassName(), gbc, 8);
        addSeparator(contentPanel, gbc, 9);
        addLabelAndValue(contentPanel, "Email:", user.getEmail(), gbc, 10);
        addSeparator(contentPanel, gbc, 11);

        // Add teacher information
        String teacherName = getTeacherName(user.getGvcn());
        addLabelAndValue(contentPanel, "Giáo viên chủ nhiệm:", teacherName, gbc, 12);
        addSeparator(contentPanel, gbc, 13);

        // Add major information
        String majorName = user.getMajor() != null ? user.getMajor().getName() : "Không xác định";
        addLabelAndValue(contentPanel, "Chuyên ngành:", majorName, gbc, 14);
        addSeparator(contentPanel, gbc, 15);

        // Add close button
        gbc.gridx = 0;
        gbc.gridy = 16;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton closeButton = createStyledButton("Đóng", new Color(108, 117, 125));
        closeButton.addActionListener(e -> dispose());
        contentPanel.add(closeButton, gbc);

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void addLabelAndValue(JPanel panel, String label, String value, GridBagConstraints gbc, int yPos) {
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel valueComponent = new JLabel(value != null ? value : "Không xác định");
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(valueComponent, gbc);
    }

    private void addSeparator(JPanel panel, GridBagConstraints gbc, int yPos) {
        gbc.gridx = 0;
        gbc.gridy = yPos;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setPreferredSize(new Dimension(1, 10));
        panel.add(separator, gbc);
        gbc.gridwidth = 1;
    }

    private String getTeacherName(String teacherId) {
        try {
            return teacherRepository.findById(teacherId)
                    .map(Teacher::getFullName)
                    .orElse("Không xác định");
        } catch (Exception e) {
            log.error("Error getting teacher name", e);
            return "Không xác định";
        }
    }

    private String getMajorName(String majorId) {
        try {
            return majorRepository.findById(majorId)
                    .map(Major::getName)
                    .orElse("Không xác định");
        } catch (Exception e) {
            log.error("Error getting major name", e);
            return "Không xác định";
        }
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(100, 35));
        return button;
    }

    public static void showDialog(JFrame parent, User user, TeacherRepository teacherRepository,
            MajorRepository majorRepository) {
        StudentDetail dialog = new StudentDetail(parent, user, teacherRepository, majorRepository);
        dialog.setVisible(true);
    }
}