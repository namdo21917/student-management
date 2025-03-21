package com.study.java.studentmanagement.swing.info;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import com.study.java.studentmanagement.dto.user.UserResponse;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.UserRepository;
import com.study.java.studentmanagement.session.UserSession;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
public class PersonalInfoPanel extends JPanel {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private JTable personalInfoTable;
    private JTable contactInfoTable;
    private JButton updateButton;
    private JButton refreshButton;
    private User currentUser;

    public PersonalInfoPanel(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        // Apply FlatLaf theme settings
        FlatLaf.setup(new FlatLightLaf());

        // Set the theme
        UIManager.put("TitlePane.background", new Color(240, 240, 240));
        UIManager.put("Toast.background", new Color(240, 240, 240));
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.margin", new Insets(4, 6, 4, 6));
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("TextField.margin", new Insets(4, 6, 4, 6));
        UIManager.put("PasswordField.margin", new Insets(4, 6, 4, 6));
        UIManager.put("ComboBox.padding", new Insets(4, 6, 4, 6));
        UIManager.put("TitlePane.unifiedBackground", false);
        UIManager.put("TitlePane.buttonSize", new Dimension(35, 23));
        UIManager.put("TitlePane.background", new Color(230, 230, 230));
        UIManager.put("TitlePane.foreground", Color.BLACK);

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel thông tin cá nhân
        JPanel personalInfoPanel = createPersonalInfoPanel();

        // Panel thông tin liên lạc
        JPanel contactInfoPanel = createContactInfoPanel();

        // Panel cho bảng
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        tablesPanel.add(personalInfoPanel);
        tablesPanel.add(contactInfoPanel);

        // Panel cho tiêu đề và các nút
        JPanel topPanel = createTopPanel();

        // Panel cho nút cập nhật
        JPanel buttonPanel = createButtonPanel();

        add(topPanel, BorderLayout.NORTH);
        add(tablesPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                "Thông tin cá nhân",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18),
                Color.BLACK));

        String[] personalColumns = { "Thông tin", "Chi tiết" };
        Object[][] personalData = {
                { "Mã", "" }, { "Họ tên", "" }, { "Giới tính", "" },
                { "CMND/CCCD", "" }, { "Lớp sinh viên", "" },
                { "Ngành học", "" }, { "Năm học", "" },
                { "Ngày sinh", "" }, { "Quyền", "" }
        };

        personalInfoTable = createTable(personalData, personalColumns);
        JScrollPane scrollPane = new JScrollPane(personalInfoTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createContactInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                "Thông tin liên lạc",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18),
                Color.BLACK));

        String[] contactColumns = { "Thông tin", "Chi tiết" };
        Object[][] contactData = {
                { "Điện thoại", "" }, { "Email cá nhân", "" },
                { "Quốc gia", "" }, { "Địa chỉ", "" }
        };

        contactInfoTable = createTable(contactData, contactColumns);
        JScrollPane scrollPane = new JScrollPane(contactInfoTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JTable createTable(Object[][] data, String[] columns) {
        JTable table = new JTable(data, columns);
        table.setEnabled(false);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setBackground(Color.WHITE);
        table.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return table;
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        refreshButton = createStyledButton("Làm mới", new Color(88, 86, 214));
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.addActionListener(e -> {
            loadData();
            showNotification("Làm mới thành công", "success");
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        updateButton = createStyledButton("Cập nhật thông tin cá nhân", new Color(88, 86, 214));
        updateButton.setPreferredSize(new Dimension(250, 40));
        updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateButton.addActionListener(e -> showUpdateModal());

        panel.add(updateButton);
        return panel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        return button;
    }

    private void showUpdateModal() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField fullNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField dobField = new JTextField();
        JComboBox<String> genderCombo = new JComboBox<>(new String[] { "Nam", "Nữ" });

        if (currentUser != null) {
            fullNameField.setText(currentUser.getFullName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhone());
            addressField.setText(currentUser.getAddress());
            dobField.setText(currentUser.getDob());
            genderCombo.setSelectedItem(currentUser.getGender());
        }

        panel.add(new JLabel("Họ tên:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Điện thoại:"));
        panel.add(phoneField);
        panel.add(new JLabel("Địa chỉ:"));
        panel.add(addressField);
        panel.add(new JLabel("Ngày sinh:"));
        panel.add(dobField);
        panel.add(new JLabel("Giới tính:"));
        panel.add(genderCombo);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        int result = JOptionPane.showConfirmDialog(
                this,
                scrollPane,
                "Cập nhật thông tin cá nhân",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            updateUserInfo(
                    fullNameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    addressField.getText(),
                    dobField.getText(),
                    (String) genderCombo.getSelectedItem());
        }
    }

    private void updateUserInfo(String fullName, String email, String phone,
            String address, String dob, String gender) {
        try {
            if (currentUser == null) {
                showNotification("Không tìm thấy thông tin người dùng", "error");
                return;
            }

            currentUser.setFullName(fullName);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            currentUser.setAddress(address);
            currentUser.setDob(dob);
            currentUser.setGender(gender);

            ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                    "/api/users/update",
                    currentUser,
                    ApiResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null
                    && response.getBody().isSuccess()) {
                showNotification("Cập nhật thông tin thành công", "success");
                loadData();
            } else {
                showNotification("Cập nhật thông tin thất bại", "error");
            }
        } catch (Exception e) {
            log.error("Error updating user info", e);
            showNotification("Lỗi khi cập nhật thông tin: " + e.getMessage(), "error");
        }
    }

    public void loadData() {
        try {
            UserResponse userResponse = UserSession.getUser();
            if (userResponse == null) {
                clearTableData();
                return;
            }

            currentUser = userRepository.findById(userResponse.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (currentUser != null) {
                updatePersonalInfoTable();
                updateContactInfoTable();
            } else {
                clearTableData();
            }
        } catch (Exception e) {
            log.error("Error loading user data", e);
            clearTableData();
        }
    }

    private void updatePersonalInfoTable() {
        personalInfoTable.setValueAt(currentUser.getMsv() != null ? currentUser.getMsv() : "N/A", 0, 1);
        personalInfoTable.setValueAt(currentUser.getFullName() != null ? currentUser.getFullName() : "N/A", 1, 1);
        personalInfoTable.setValueAt(currentUser.getGender() != null ? currentUser.getGender() : "N/A", 2, 1);
        personalInfoTable.setValueAt("N/A", 3, 1);
        personalInfoTable.setValueAt(currentUser.getClassName() != null ? currentUser.getClassName() : "N/A", 4, 1);
        personalInfoTable.setValueAt(currentUser.getMajorName() != null ? currentUser.getMajorName() : "N/A", 5, 1);
        personalInfoTable.setValueAt(currentUser.getYear() != null ? currentUser.getYear() : "N/A", 6, 1);
        personalInfoTable.setValueAt(currentUser.getDob() != null ? currentUser.getDob() : "N/A", 7, 1);
        personalInfoTable.setValueAt(currentUser.isAdmin() ? "Admin" : "Student", 8, 1);
    }

    private void updateContactInfoTable() {
        contactInfoTable.setValueAt(currentUser.getPhone() != null ? currentUser.getPhone() : "N/A", 0, 1);
        contactInfoTable.setValueAt(currentUser.getEmail() != null ? currentUser.getEmail() : "N/A", 1, 1);
        contactInfoTable.setValueAt(currentUser.getCountry() != null ? currentUser.getCountry() : "N/A", 2, 1);
        contactInfoTable.setValueAt(currentUser.getAddress() != null ? currentUser.getAddress() : "N/A", 3, 1);
    }

    private void clearTableData() {
        for (int row = 0; row < personalInfoTable.getRowCount(); row++) {
            personalInfoTable.setValueAt("N/A", row, 1);
        }
        for (int row = 0; row < contactInfoTable.getRowCount(); row++) {
            contactInfoTable.setValueAt("N/A", row, 1);
        }
    }

    private void showNotification(String message, String type) {
        Color backgroundColor = type.equals("success") ? new Color(40, 167, 69) : new Color(220, 53, 69);
        JOptionPane.showMessageDialog(
                this,
                message,
                type.equals("success") ? "Thành công" : "Lỗi",
                type.equals("success") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    }
}