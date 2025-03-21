package com.study.java.studentmanagement.swing.student;

import com.formdev.flatlaf.FlatClientProperties;
import com.study.java.studentmanagement.model.Major;
import com.study.java.studentmanagement.model.Teacher;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.MajorRepository;
import com.study.java.studentmanagement.repository.TeacherRepository;
import com.study.java.studentmanagement.repository.UserRepository;
import com.study.java.studentmanagement.service.ApiService;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

@Slf4j
public class AddStudentForm extends JDialog {
    private final ApiService apiService;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final MajorRepository majorRepository;
    private final StudentPanel studentPanel;

    private JTextField nameField;
    private JTextField msvField;
    private JTextField yearField;
    private JComboBox<String> gvcnComboBox;
    private JTextField genderField;
    private JTextField classField;
    private JTextField emailField;
    private JComboBox<String> majorComboBox;
    private List<Teacher> teachers;
    private List<Major> majors;

    public AddStudentForm(JFrame parent, StudentPanel studentPanel, ApiService apiService,
            UserRepository userRepository, TeacherRepository teacherRepository,
            MajorRepository majorRepository) {
        super(parent, "Thêm sinh viên", true);
        this.apiService = apiService;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.majorRepository = majorRepository;
        this.studentPanel = studentPanel;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initializeUI();
        loadData();
    }

    private void initializeUI() {
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().setLayout(new BorderLayout());

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tên:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField();
        nameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên sinh viên");
        formPanel.add(nameField, gbc);

        // MSV field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Mã sinh viên:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        msvField = new JTextField();
        msvField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã sinh viên");
        formPanel.add(msvField, gbc);

        // Year field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Năm:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        yearField = new JTextField();
        yearField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập năm học");
        formPanel.add(yearField, gbc);

        // Teacher field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Giáo viên chủ nhiệm:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gvcnComboBox = new JComboBox<>();
        formPanel.add(gvcnComboBox, gbc);

        // Gender field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Giới tính:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        genderField = new JTextField();
        genderField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập giới tính");
        formPanel.add(genderField, gbc);

        // Class field
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Lớp:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        classField = new JTextField();
        classField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập lớp");
        formPanel.add(classField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField();
        emailField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập email");
        formPanel.add(emailField, gbc);

        // Major field
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Chuyên ngành:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        majorComboBox = new JComboBox<>();
        formPanel.add(majorComboBox, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Thêm", new Color(40, 167, 69));
        JButton cancelButton = createStyledButton("Hủy", new Color(220, 53, 69));

        saveButton.addActionListener(e -> handleSave());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
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

    private void loadData() {
        try {
            teachers = teacherRepository.findAll();
            majors = majorRepository.findAll();

            gvcnComboBox.removeAllItems();
            for (Teacher teacher : teachers) {
                gvcnComboBox.addItem(teacher.getFullName());
            }

            majorComboBox.removeAllItems();
            for (Major major : majors) {
                majorComboBox.addItem(major.getName());
            }
        } catch (Exception e) {
            log.error("Error loading data", e);
            showError("Lỗi khi tải dữ liệu");
        }
    }

    private void handleSave() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Create user object
            User user = new User();
            user.setFullName(nameField.getText().trim());
            user.setMsv(msvField.getText().trim());
            user.setYear(yearField.getText().trim());
            user.setGender(genderField.getText().trim());
            user.setClassName(classField.getText().trim());
            user.setEmail(emailField.getText().trim());

            // Set teacher
            String selectedTeacherName = (String) gvcnComboBox.getSelectedItem();
            for (Teacher teacher : teachers) {
                if (teacher.getFullName().equals(selectedTeacherName)) {
                    user.setGvcn(teacher.getId());
                    break;
                }
            }

            // Set major
            String selectedMajorName = (String) majorComboBox.getSelectedItem();
            for (Major major : majors) {
                if (major.getName().equals(selectedMajorName)) {
                    user.setMajor(major);
                    break;
                }
            }

            // Call API to save
            ApiResponse<User> response = apiService.post(
                    "/api/users/save",
                    user,
                    new ParameterizedTypeReference<ApiResponse<User>>() {
                    });

            if (response != null) {
                showSuccess("Thêm sinh viên thành công");
                studentPanel.loadData();
                dispose();
            } else {
                showError("Lỗi khi thêm sinh viên");
            }
        } catch (Exception e) {
            log.error("Error saving student", e);
            showError("Lỗi khi thêm sinh viên: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        String name = nameField.getText().trim();
        String msv = msvField.getText().trim();
        String year = yearField.getText().trim();
        String gender = genderField.getText().trim();
        String className = classField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty()) {
            showError("Tên không được để trống");
            return false;
        }

        if (msv.isEmpty()) {
            showError("Mã sinh viên không được để trống");
            return false;
        }

        if (year.isEmpty()) {
            showError("Năm học không được để trống");
            return false;
        }

        if (gender.isEmpty()) {
            showError("Giới tính không được để trống");
            return false;
        }

        if (className.isEmpty()) {
            showError("Lớp không được để trống");
            return false;
        }

        if (email.isEmpty()) {
            showError("Email không được để trống");
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Email không hợp lệ");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showDialog(JFrame parent, StudentPanel studentPanel, ApiService apiService,
            UserRepository userRepository, TeacherRepository teacherRepository,
            MajorRepository majorRepository) {
        AddStudentForm dialog = new AddStudentForm(parent, studentPanel, apiService,
                userRepository, teacherRepository, majorRepository);
        dialog.setVisible(true);
    }
}