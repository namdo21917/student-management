package com.study.java.studentmanagement.swing.semester;

import com.formdev.flatlaf.FlatClientProperties;
import com.study.java.studentmanagement.model.Semester;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class SemesterForm extends JDialog {
    private final RestTemplate restTemplate;
    private final Semester semester;
    private JTextField semesterField;
    private JTextField groupField;
    private JTextField yearField;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    private JCheckBox activeCheckBox;
    private boolean isConfirmed = false;

    private SemesterForm(JFrame parent, Semester semester, RestTemplate restTemplate) {
        super(parent, semester == null ? "Thêm học kỳ mới" : "Cập nhật học kỳ", true);
        this.restTemplate = restTemplate;
        this.semester = semester != null ? semester : new Semester();

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initializeUI();
        loadData();
    }

    private void initializeUI() {
        // Set border for content pane
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().setLayout(new BorderLayout());

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Semester field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Học kỳ:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        semesterField = new JTextField();
        semesterField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập học kỳ");
        formPanel.add(semesterField, gbc);

        // Group field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Nhóm:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        groupField = new JTextField();
        groupField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập nhóm");
        formPanel.add(groupField, gbc);

        // Year field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Năm học:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        yearField = new JTextField();
        yearField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập năm học");
        formPanel.add(yearField, gbc);

        // Start date field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Ngày bắt đầu:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "dd/MM/yyyy");
        startDateSpinner.setEditor(startDateEditor);
        formPanel.add(startDateSpinner, gbc);

        // End date field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Ngày kết thúc:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "dd/MM/yyyy");
        endDateSpinner.setEditor(endDateEditor);
        formPanel.add(endDateSpinner, gbc);

        // Active checkbox
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        activeCheckBox = new JCheckBox("Đang hoạt động");
        formPanel.add(activeCheckBox, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Lưu", new Color(40, 167, 69));
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
        if (semester.getId() != null) {
            semesterField.setText(semester.getSemester());
            groupField.setText(semester.getGroup());
            yearField.setText(semester.getYear());
            if (semester.getStartDate() != null) {
                startDateSpinner.setValue(semester.getStartDate());
            }
            if (semester.getEndDate() != null) {
                endDateSpinner.setValue(semester.getEndDate());
            }
            activeCheckBox.setSelected(semester.isActive());
        }
    }

    private void handleSave() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Update semester object
            semester.setSemester(semesterField.getText().trim());
            semester.setGroup(groupField.getText().trim());
            semester.setYear(yearField.getText().trim());
            semester.setStartDate(LocalDateTime.ofInstant(
                    ((java.util.Date) startDateSpinner.getValue()).toInstant(),
                    java.time.ZoneId.systemDefault()));
            semester.setEndDate(LocalDateTime.ofInstant(
                    ((java.util.Date) endDateSpinner.getValue()).toInstant(),
                    java.time.ZoneId.systemDefault()));
            semester.setActive(activeCheckBox.isSelected());

            // Call API to save
            ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                    "/api/semesters/save",
                    semester,
                    ApiResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                isConfirmed = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Lưu thông tin thất bại",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            log.error("Error saving semester", e);
            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi khi lưu thông tin: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput() {
        String semester = semesterField.getText().trim();
        String group = groupField.getText().trim();
        String year = yearField.getText().trim();

        if (semester.isEmpty()) {
            showError("Học kỳ không được để trống");
            return false;
        }

        if (group.isEmpty()) {
            showError("Nhóm không được để trống");
            return false;
        }

        if (year.isEmpty()) {
            showError("Năm học không được để trống");
            return false;
        }

        if (!year.matches("\\d{4}-\\d{4}")) {
            showError("Năm học phải có định dạng YYYY-YYYY");
            return false;
        }

        java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
        java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();

        if (startDate.after(endDate)) {
            showError("Ngày bắt đầu phải trước ngày kết thúc");
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

    public static Semester showDialog(JFrame parent, Semester semester, RestTemplate restTemplate) {
        SemesterForm dialog = new SemesterForm(parent, semester, restTemplate);
        dialog.setVisible(true);
        return dialog.isConfirmed ? dialog.semester : null;
    }
}