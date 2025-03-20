package com.study.java.studentmanagement.swing.score;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.study.java.studentmanagement.model.Semester;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.SemesterRepository;
import com.study.java.studentmanagement.repository.UserRepository;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;
import java.util.Map;

@Slf4j
public class ScoreReportPanel extends JPanel {
    private final SemesterRepository semesterRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private JComboBox<String> semesterComboBox;
    private JComboBox<String> programComboBox;
    private DefaultTableModel model;
    private JTable table;
    private JLabel totalStudentsLabel;
    private JLabel passedStudentsLabel;
    private JLabel passRateLabel;
    private JLabel averageScoreLabel;

    public ScoreReportPanel(SemesterRepository semesterRepository, UserRepository userRepository,
            RestTemplate restTemplate) {
        this.semesterRepository = semesterRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        initUI();
        loadSemesterData();
        loadProgramData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Apply FlatLaf theme
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            log.error("Error setting up FlatLaf theme", e);
        }

        // Header Panel
        JPanel headerPanel = createHeaderPanel();

        // Content Panel
        JPanel contentPanel = createContentPanel();

        // Stats Panel
        JPanel statsPanel = createStatsPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);

        // Add action listeners
        semesterComboBox.addActionListener(e -> fetchTranscriptData());
        programComboBox.addActionListener(e -> fetchTranscriptData());
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel programLabel = new JLabel("Chương trình đào tạo:");
        programComboBox = new JComboBox<>();

        JLabel semesterLabel = new JLabel("Học kỳ:");
        semesterComboBox = new JComboBox<>(new String[] { "Tất cả" });

        headerPanel.add(programLabel);
        headerPanel.add(programComboBox);
        headerPanel.add(Box.createHorizontalStrut(20));
        headerPanel.add(semesterLabel);
        headerPanel.add(semesterComboBox);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Create table
        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[] {
                "STT", "Mã môn học", "Tên môn học", "Số TC",
                "Điểm quá trình", "Điểm cuối kỳ", "Điểm tổng kết", "Kết quả"
        });
        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Customize table header
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0x2A3F54));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Customize table cells
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(new Font("Arial", Font.PLAIN, 14));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 220, 220)));
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        totalStudentsLabel = new JLabel("Tổng số sinh viên: 0");
        passedStudentsLabel = new JLabel("Số sinh viên đạt: 0");
        passRateLabel = new JLabel("Tỷ lệ đạt: 0%");
        averageScoreLabel = new JLabel("Điểm trung bình: 0.0");

        statsPanel.add(totalStudentsLabel);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(passedStudentsLabel);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(passRateLabel);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(averageScoreLabel);

        return statsPanel;
    }

    private void loadSemesterData() {
        try {
            List<Semester> semesters = semesterRepository.findAll();
            for (Semester semester : semesters) {
                String comboBoxItem = semester.getSemester() + " - " + semester.getGroup() + " - " + semester.getYear();
                semesterComboBox.addItem(comboBoxItem);
            }
        } catch (Exception e) {
            log.error("Error loading semester data", e);
            showError("Lỗi khi tải dữ liệu học kỳ");
        }
    }

    private void loadProgramData() {
        try {
            User currentUser = userRepository.findCurrentUser();
            if (currentUser != null && currentUser.getMajor() != null) {
                programComboBox.addItem(currentUser.getMajor().getName());
            }
        } catch (Exception e) {
            log.error("Error loading program data", e);
            showError("Lỗi khi tải dữ liệu chương trình");
        }
    }

    private void fetchTranscriptData() {
        try {
            String selectedSemester = (String) semesterComboBox.getSelectedItem();
            String semesterId = "Tất cả".equals(selectedSemester) ? null : getSemesterIdFromComboBox(selectedSemester);

            User currentUser = userRepository.findCurrentUser();
            if (currentUser == null) {
                showError("Không tìm thấy thông tin người dùng");
                return;
            }

            String url = buildTranscriptUrl(currentUser.getId(), semesterId);
            ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null
                    && response.getBody().isSuccess()) {
                updateTableData(response.getBody());
                updateStats(response.getBody());
            } else {
                String errorMessage = response.getBody() != null ? response.getBody().getMessage()
                        : "Lỗi không xác định";
                log.error("API request failed: {}", errorMessage);
                showError("Lỗi khi tải dữ liệu điểm: " + errorMessage);
            }
        } catch (Exception e) {
            log.error("Error fetching transcript data", e);
            showError("Lỗi khi tải dữ liệu điểm: " + e.getMessage());
        }
    }

    private String buildTranscriptUrl(String userId, String semesterId) {
        String baseUrl = "/api/transcript/student/" + userId;
        return semesterId != null ? baseUrl + "/semester/" + semesterId : baseUrl;
    }

    private void updateTableData(ApiResponse response) {
        try {
            model.setRowCount(0);
            if (response.getData() instanceof List) {
                List<?> transcriptData = (List<?>) response.getData();
                for (int i = 0; i < transcriptData.size(); i++) {
                    Object item = transcriptData.get(i);
                    if (item instanceof Map) {
                        Map<?, ?> transcriptItem = (Map<?, ?>) item;
                        model.addRow(new Object[] {
                                i + 1,
                                transcriptItem.get("courseCode"),
                                transcriptItem.get("courseName"),
                                transcriptItem.get("credits"),
                                transcriptItem.get("midtermScore"),
                                transcriptItem.get("finalScore"),
                                transcriptItem.get("averageScore"),
                                transcriptItem.get("status")
                        });
                    }
                }
            } else {
                log.warn("Unexpected data type in API response: {}", response.getData().getClass());
                showError("Định dạng dữ liệu không hợp lệ");
            }
        } catch (Exception e) {
            log.error("Error updating table data", e);
            showError("Lỗi khi cập nhật dữ liệu bảng");
        }
    }

    private void updateStats(ApiResponse response) {
        try {
            if (response.getData() instanceof Map) {
                Map<?, ?> stats = (Map<?, ?>) response.getData();
                totalStudentsLabel.setText("Tổng số sinh viên: " + stats.get("totalStudents"));
                passedStudentsLabel.setText("Số sinh viên đạt: " + stats.get("passedStudents"));
                passRateLabel.setText("Tỷ lệ đạt: " + stats.get("passRate") + "%");
                averageScoreLabel.setText("Điểm trung bình: " + stats.get("averageScore"));
            } else {
                log.warn("Unexpected data type in API response for stats: {}", response.getData().getClass());
                showError("Định dạng dữ liệu thống kê không hợp lệ");
            }
        } catch (Exception e) {
            log.error("Error updating statistics", e);
            showError("Lỗi khi cập nhật thống kê");
        }
    }

    private String getSemesterIdFromComboBox(String semesterComboBoxItem) {
        try {
            List<Semester> semesters = semesterRepository.findAll();
            for (Semester semester : semesters) {
                String description = semester.getSemester() + " - " + semester.getGroup() + " - " + semester.getYear();
                if (description.equals(semesterComboBoxItem)) {
                    return semester.getId();
                }
            }
        } catch (Exception e) {
            log.error("Error getting semester ID", e);
        }
        return null;
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        });
    }
}