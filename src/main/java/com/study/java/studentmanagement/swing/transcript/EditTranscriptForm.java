package com.study.java.studentmanagement.swing.transcript;

import com.formdev.flatlaf.FlatClientProperties;
import com.study.java.studentmanagement.dto.transcript.TranscriptRequest;
import com.study.java.studentmanagement.model.Semester;
import com.study.java.studentmanagement.model.Transcript;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.SemesterRepository;
import com.study.java.studentmanagement.repository.TranscriptRepository;
import com.study.java.studentmanagement.repository.UserRepository;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
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
public class EditTranscriptForm extends JDialog {
    private final RestTemplate restTemplate;
    private final TranscriptRepository transcriptRepository;
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;
    private final TranscriptPanel parentPanel;
    private final Transcript transcript;

    private JComboBox<String> studentComboBox;
    private JComboBox<String> semesterComboBox;
    private JButton saveButton;
    private JButton cancelButton;

    public EditTranscriptForm(JFrame parent, Transcript transcript, TranscriptPanel parentPanel,
            RestTemplate restTemplate,
            TranscriptRepository transcriptRepository,
            UserRepository userRepository,
            SemesterRepository semesterRepository) {
        super(parent, "Chỉnh sửa bảng điểm", true);
        this.transcript = transcript;
        this.parentPanel = parentPanel;
        this.restTemplate = restTemplate;
        this.transcriptRepository = transcriptRepository;
        this.userRepository = userRepository;
        this.semesterRepository = semesterRepository;

        initializeUI();
        loadData();
        populateFields();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize components
        studentComboBox = new JComboBox<>();
        semesterComboBox = new JComboBox<>();
        saveButton = createStyledButton("Lưu", new Color(40, 167, 69));
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
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Set button actions
        saveButton.addActionListener(e -> updateTranscript());
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

    private void populateFields() {
        if (transcript != null) {
            // Get student display from transcript
            String studentDisplay = transcript.getStudentName() + " - " + transcript.getStudentCode();
            studentComboBox.setSelectedItem(studentDisplay);

            // Get semester display from transcript
            String semesterDisplay = transcript.getSemesterName();
            semesterComboBox.setSelectedItem(semesterDisplay);
        }
    }

    private void updateTranscript() {
        try {
            String studentDisplay = (String) studentComboBox.getSelectedItem();
            String semesterDisplay = (String) semesterComboBox.getSelectedItem();

            String studentId = getStudentIdByDisplay(studentDisplay);
            String semesterId = getSemesterIdByDisplay(semesterDisplay);

            if (studentId != null && semesterId != null) {
                // Get student and semester information
                User student = userRepository.findById(studentId).orElse(null);
                Semester semester = semesterRepository.findById(semesterId).orElse(null);

                if (student != null && semester != null) {
                    // Create request object
                    TranscriptRequest request = new TranscriptRequest();
                    request.setStudentId(studentId);
                    request.setStudentName(student.getFullName());
                    request.setStudentCode(student.getMsv());
                    request.setSemesterId(semesterId);
                    request.setSemesterName(
                            semester.getSemester() + " - " + semester.getGroup() + " - Năm học: " + semester.getYear());

                    HttpEntity<TranscriptRequest> requestEntity = new HttpEntity<>(request);

                    ResponseEntity<ApiResponse<Transcript>> response = restTemplate.exchange(
                            "/api/transcript/update/" + transcript.getId(),
                            org.springframework.http.HttpMethod.PUT,
                            requestEntity,
                            new org.springframework.core.ParameterizedTypeReference<ApiResponse<Transcript>>() {
                            });

                    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                        showSuccess("Cập nhật bảng điểm thành công");
                        parentPanel.loadData();
                        dispose();
                    } else {
                        showError("Cập nhật bảng điểm thất bại");
                    }
                } else {
                    showError("Không tìm thấy thông tin sinh viên hoặc học kỳ");
                }
            } else {
                showError("Dữ liệu không hợp lệ");
            }
        } catch (Exception e) {
            log.error("Error updating transcript", e);
            showError("Lỗi khi cập nhật bảng điểm: " + e.getMessage());
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

    private String getStudentDisplayById(String studentId) {
        List<User> students = userRepository.findAll();
        for (User student : students) {
            if (student.getId().equals(studentId)) {
                return student.getFullName() + " - " + student.getMsv();
            }
        }
        return null;
    }

    private String getSemesterDisplayById(String semesterId) {
        List<Semester> semesters = semesterRepository.findAll();
        for (Semester semester : semesters) {
            if (semester.getId().equals(semesterId)) {
                return semester.getSemester() + " - " + semester.getGroup() + " - Năm học: " + semester.getYear();
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

    public static void showDialog(JFrame parent, Transcript transcript, TranscriptPanel parentPanel,
            RestTemplate restTemplate,
            TranscriptRepository transcriptRepository,
            UserRepository userRepository,
            SemesterRepository semesterRepository) {
        EditTranscriptForm dialog = new EditTranscriptForm(
                parent, transcript, parentPanel, restTemplate, transcriptRepository, userRepository,
                semesterRepository);
        dialog.setVisible(true);
    }
}