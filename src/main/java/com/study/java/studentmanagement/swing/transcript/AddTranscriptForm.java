package com.study.java.studentmanagement.swing.transcript;

import com.formdev.flatlaf.FlatClientProperties;
import com.study.java.studentmanagement.model.Semester;
import com.study.java.studentmanagement.model.Transcript;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.SemesterRepository;
import com.study.java.studentmanagement.repository.TranscriptRepository;
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
public class AddTranscriptForm extends JDialog {
    private final RestTemplate restTemplate;
    private final TranscriptRepository transcriptRepository;
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;
    private final TranscriptPanel parentPanel;

    private JComboBox<String> studentComboBox;
    private JComboBox<String> semesterComboBox;
    private JButton addButton;
    private JButton cancelButton;

    public AddTranscriptForm(JFrame parent, TranscriptPanel parentPanel,
            RestTemplate restTemplate,
            TranscriptRepository transcriptRepository,
            UserRepository userRepository,
            SemesterRepository semesterRepository) {
        super(parent, "Thêm bảng điểm mới", true);
        this.parentPanel = parentPanel;
        this.restTemplate = restTemplate;
        this.transcriptRepository = transcriptRepository;
        this.userRepository = userRepository;
        this.semesterRepository = semesterRepository;

        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize components
        studentComboBox = new JComboBox<>();
        semesterComboBox = new JComboBox<>();
        addButton = createStyledButton("Thêm", new Color(40, 167, 69));
        cancelButton = createStyledButton("Hủy", new Color(220, 53, 69));

        // Add components to the form
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Sinh viên:"), gbc);

        gbc.gridx = 1;
        add(studentComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Học kỳ:"), gbc);

        gbc.gridx = 1;
        add(semesterComboBox, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Set button actions
        addButton.addActionListener(e -> addTranscript());
        cancelButton.addActionListener(e -> dispose());

        // Set dialog properties
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
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
            List<User> students = userRepository.findAll();
            List<Semester> semesters = semesterRepository.findAll();

            // Populate student combo box
            for (User student : students) {
                studentComboBox.addItem(student.getFullName() + " - " + student.getMsv());
            }

            // Populate semester combo box
            for (Semester semester : semesters) {
                semesterComboBox.addItem(
                        semester.getSemester() + " - " + semester.getGroup() + " - Năm học: " + semester.getYear());
            }
        } catch (Exception e) {
            log.error("Error loading data", e);
            showError("Lỗi khi tải dữ liệu");
        }
    }

    private void addTranscript() {
        try {
            String studentDisplay = (String) studentComboBox.getSelectedItem();
            String semesterDisplay = (String) semesterComboBox.getSelectedItem();

            String studentId = getStudentIdByDisplay(studentDisplay);
            String semesterId = getSemesterIdByDisplay(semesterDisplay);

            if (studentId != null && semesterId != null) {
                Transcript newTranscript = new Transcript(studentId, semesterId);
                ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                        "/api/transcripts",
                        newTranscript,
                        ApiResponse.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    showSuccess("Thêm bảng điểm thành công");
                    parentPanel.loadData();
                    dispose();
                } else {
                    showError("Thêm bảng điểm thất bại");
                }
            } else {
                showError("Dữ liệu không hợp lệ");
            }
        } catch (Exception e) {
            log.error("Error adding transcript", e);
            showError("Lỗi khi thêm bảng điểm");
        }
    }

    private String getStudentIdByDisplay(String displayText) {
        List<User> students = userRepository.findAll();
        for (User student : students) {
            if ((student.getFullName() + " - " + student.getMsv()).equals(displayText)) {
                return student.getId();
            }
        }
        return null;
    }

    private String getSemesterIdByDisplay(String displayText) {
        List<Semester> semesters = semesterRepository.findAll();
        for (Semester semester : semesters) {
            if ((semester.getSemester() + " - " + semester.getGroup() + " - Năm học: " + semester.getYear())
                    .equals(displayText)) {
                return semester.getId();
            }
        }
        return null;
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

    public static void showDialog(JFrame parent, TranscriptPanel parentPanel,
            RestTemplate restTemplate,
            TranscriptRepository transcriptRepository,
            UserRepository userRepository,
            SemesterRepository semesterRepository) {
        AddTranscriptForm dialog = new AddTranscriptForm(
                parent, parentPanel, restTemplate, transcriptRepository, userRepository, semesterRepository);
        dialog.setVisible(true);
    }
}