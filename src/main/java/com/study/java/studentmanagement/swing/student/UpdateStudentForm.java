package com.study.java.studentmanagement.swing.student;

import com.formdev.flatlaf.FlatClientProperties;
import com.study.java.studentmanagement.model.Major;
import com.study.java.studentmanagement.model.Teacher;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.MajorRepository;
import com.study.java.studentmanagement.repository.TeacherRepository;
import com.study.java.studentmanagement.repository.UserRepository;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

@Slf4j
@Component
public class UpdateStudentForm extends JDialog {
    private final RestTemplate restTemplate;
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
    private final User user;

    public UpdateStudentForm(JFrame parent, User user, StudentPanel studentPanel,
            RestTemplate restTemplate, UserRepository userRepository,
            TeacherRepository teacherRepository, MajorRepository majorRepository) {
        super(parent, "Cập nhật sinh viên", true);
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.majorRepository = majorRepository;
        this.studentPanel = studentPanel;
        this.user = user;

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
        nameField = new JTextField(user.getFullName());
        nameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên sinh viên");
        formPanel.add(nameField, gbc);

        // MSV field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Mã sinh viên:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        msvField = new JTextField(user.getMsv());
        msvField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã sinh viên");
        formPanel.add(msvField, gbc);

        // Year field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Năm:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        yearField = new JTextField(user.getYear());
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
        genderField = new JTextField(user.getGender());
        genderField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập giới tính");
        formPanel.add(genderField, gbc);

        // Class field
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Lớp:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        classField = new JTextField(user.getClassName());
        classField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập lớp");
        formPanel.add(classField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField(user.getEmail());
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
        JButton updateButton = createStyledButton("Cập nhật", new Color(40, 167, 69));
        JButton cancelButton = createStyledButton("Hủy", new Color(220, 53, 69));

        updateButton.addActionListener(e -> handleUpdate());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(updateButton);
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
                if (teacher.getId().equals(user.getGvcn())) {
                    gvcnComboBox.setSelectedItem(teacher.getFullName());
                }
            }

            majorComboBox.removeAllItems();
            for (Major major : majors) {
                majorComboBox.addItem(major.getName());
                if (major.getId().equals(user.getMajorId())) {
                    majorComboBox.setSelectedItem(major.getName());
                }
            }
        } catch (Exception e) {
            log.error("Error loading data", e);
            showError("Lỗi khi tải dữ liệu");
        }
    }

    private void handleUpdate() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Update user object
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
                    user.setMajorId(major.getId());
                    break;
                }
            }

            // Call API to update
            ResponseEntity<ApiResponse> response = restTemplate.exchange(
                    "/api/users/" + user.getId(),
                    org.springframework.http.HttpMethod.PUT,
                    user,
                    ApiResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                showSuccess("Cập nhật sinh viên thành công");
                studentPanel.loadData();
                dispose();
            } else {
                showError("Cập nhật thông tin thất bại");
            }
        } catch (Exception e) {
            log.error("Error updating student", e);
            showError("Lỗi khi cập nhật thông tin: " + e.getMessage());
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

    public static void showDialog(JFrame parent, User user, StudentPanel studentPanel,
            RestTemplate restTemplate, UserRepository userRepository,
            TeacherRepository teacherRepository, MajorRepository majorRepository) {
        UpdateStudentForm dialog = new UpdateStudentForm(parent, user, studentPanel,
                restTemplate, userRepository, teacherRepository, majorRepository);
        dialog.setVisible(true);
    }
}