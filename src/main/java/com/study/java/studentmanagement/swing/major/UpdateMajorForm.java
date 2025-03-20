package com.study.java.studentmanagement.swing.major;

import com.formdev.flatlaf.FlatClientProperties;
import com.study.java.studentmanagement.model.Major;
import com.study.java.studentmanagement.repository.MajorRepository;
import com.study.java.studentmanagement.util.ApiResponse;
import com.study.java.studentmanagement.util.ValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Slf4j
public class UpdateMajorForm extends JDialog {
    private final MajorRepository majorRepository;
    private final RestTemplate restTemplate;
    private final Major major;
    private JTextField codeField;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JCheckBox activeCheckBox;
    private boolean isConfirmed = false;

    private UpdateMajorForm(JFrame parent, Major major, MajorRepository majorRepository, RestTemplate restTemplate) {
        super(parent, major == null ? "Thêm ngành mới" : "Cập nhật ngành", true);
        this.majorRepository = majorRepository;
        this.restTemplate = restTemplate;
        this.major = major != null ? major : new Major();

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Code field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Mã ngành:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        codeField = new JTextField();
        codeField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã ngành");
        formPanel.add(codeField, gbc);

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Tên ngành:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField();
        nameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tên ngành");
        formPanel.add(nameField, gbc);

        // Description field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Mô tả:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mô tả ngành");
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);

        // Active checkbox
        gbc.gridx = 1;
        gbc.gridy = 3;
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
        if (major.getId() != null) {
            codeField.setText(major.getCode());
            nameField.setText(major.getName());
            descriptionArea.setText(major.getDescription());
            activeCheckBox.setSelected(major.isActive());
            codeField.setEnabled(false); // Disable code field when editing
        }
    }

    private void handleSave() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }

            // Update major object
            major.setCode(codeField.getText().trim());
            major.setName(nameField.getText().trim());
            major.setDescription(descriptionArea.getText().trim());
            major.setActive(activeCheckBox.isSelected());

            // Call API to save
            ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                    "/api/majors/save",
                    major,
                    ApiResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null
                    && response.getBody().isSuccess()) {
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
            log.error("Error saving major", e);
            JOptionPane.showMessageDialog(
                    this,
                    "Lỗi khi lưu thông tin: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateInput() {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (code.isEmpty()) {
            showError("Mã ngành không được để trống");
            return false;
        }

        if (!ValidationUtil.isValidMajorCode(code)) {
            showError("Mã ngành chỉ được chứa chữ cái in hoa và số");
            return false;
        }

        if (name.isEmpty()) {
            showError("Tên ngành không được để trống");
            return false;
        }

        if (name.length() > 100) {
            showError("Tên ngành không được vượt quá 100 ký tự");
            return false;
        }

        if (description.length() > 500) {
            showError("Mô tả không được vượt quá 500 ký tự");
            return false;
        }

        // Check if code exists (only for new majors)
        if (major.getId() == null && majorRepository.existsByCode(code)) {
            showError("Mã ngành đã tồn tại");
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

    public static Major showDialog(JFrame parent, Major major, MajorRepository majorRepository,
            RestTemplate restTemplate) {
        UpdateMajorForm dialog = new UpdateMajorForm(parent, major, majorRepository, restTemplate);
        dialog.setVisible(true);
        return dialog.isConfirmed ? dialog.major : null;
    }
}