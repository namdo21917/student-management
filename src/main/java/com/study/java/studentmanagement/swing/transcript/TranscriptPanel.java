package com.study.java.studentmanagement.swing.transcript;

import com.formdev.flatlaf.FlatClientProperties;
import com.study.java.studentmanagement.model.Semester;
import com.study.java.studentmanagement.model.Transcript;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.SemesterRepository;
import com.study.java.studentmanagement.repository.TranscriptRepository;
import com.study.java.studentmanagement.repository.UserRepository;
import com.study.java.studentmanagement.repository.CourseRepository;
import com.study.java.studentmanagement.repository.GradeRepository;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

@Slf4j
@Component
public class TranscriptPanel extends JPanel {
    private final RestTemplate restTemplate;
    private final TranscriptRepository transcriptRepository;
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;

    private JTextField searchField;
    private JButton searchButton;
    private JButton reloadButton;
    private JButton addTranscriptButton;
    private JTable transcriptTable;
    private DefaultTableModel tableModel;

    private List<User> students;
    private List<Semester> semesters;

    public TranscriptPanel(RestTemplate restTemplate,
            TranscriptRepository transcriptRepository,
            UserRepository userRepository,
            SemesterRepository semesterRepository,
            CourseRepository courseRepository,
            GradeRepository gradeRepository) {
        this.restTemplate = restTemplate;
        this.transcriptRepository = transcriptRepository;
        this.userRepository = userRepository;
        this.semesterRepository = semesterRepository;
        this.courseRepository = courseRepository;
        this.gradeRepository = gradeRepository;

        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Title Panel
        JLabel titleLabel = new JLabel("Thông tin bảng điểm", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Top Panel with search and buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchField = new JTextField(20);
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm...");
        searchButton = createStyledButton("Tìm kiếm", new Color(88, 86, 214));
        reloadButton = createStyledButton("Tải lại", new Color(88, 86, 214));
        addTranscriptButton = createStyledButton("Thêm bảng điểm", new Color(40, 167, 69));

        searchButton.addActionListener(e -> searchTranscripts());
        reloadButton.addActionListener(e -> {
            searchField.setText("");
            loadData();
        });
        addTranscriptButton.addActionListener(e -> openAddTranscriptForm());

        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(reloadButton);
        topPanel.add(addTranscriptButton);

        // Table
        String[] columnNames = { "STT", "ID", "Mã sinh viên", "Sinh viên", "Học kỳ", "Thao tác" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only allow editing of action column
            }
        };

        transcriptTable = new JTable(tableModel);
        transcriptTable.setRowHeight(40);
        transcriptTable.setIntercellSpacing(new Dimension(0, 1));
        transcriptTable.setGridColor(new Color(220, 220, 220));

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < transcriptTable.getColumnCount(); i++) {
            transcriptTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Hide ID column
        transcriptTable.getColumnModel().getColumn(1).setMinWidth(0);
        transcriptTable.getColumnModel().getColumn(1).setMaxWidth(0);
        transcriptTable.getColumnModel().getColumn(1).setWidth(0);

        // Set up action column
        transcriptTable.getColumn("Thao tác").setCellRenderer(new ButtonRenderer());
        transcriptTable.getColumn("Thao tác").setCellEditor(new ButtonEditor(new JCheckBox()));

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(transcriptTable), BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }

    public void loadData() {
        try {
            List<Transcript> transcripts = transcriptRepository.findAll();
            tableModel.setRowCount(0);
            int stt = 1;

            for (Transcript transcript : transcripts) {
                String studentName = getStudentNameById(transcript.getStudent().getId());
                String semesterName = getSemesterNameById(transcript.getSemesterId());

                tableModel.addRow(new Object[] {
                        stt++,
                        transcript.getId(),
                        getStudentCodeById(transcript.getStudent().getId()),
                        studentName != null ? studentName : "Không xác định",
                        semesterName != null ? semesterName : "Không xác định",
                        "Xem, Sửa, Xóa"
                });
            }
        } catch (Exception e) {
            log.error("Error loading transcripts", e);
            showError("Lỗi khi tải dữ liệu bảng điểm");
        }
    }

    private void searchTranscripts() {
        try {
            String keyword = searchField.getText().toLowerCase();
            List<Transcript> transcripts = transcriptRepository.findAll();
            tableModel.setRowCount(0);
            int stt = 1;

            for (Transcript transcript : transcripts) {
                String studentName = getStudentNameById(transcript.getStudent().getId());
                String studentCode = getStudentCodeById(transcript.getStudent().getId());
                String semesterName = getSemesterNameById(transcript.getSemesterId());

                if (studentName != null && studentName.toLowerCase().contains(keyword) ||
                        studentCode != null && studentCode.toLowerCase().contains(keyword) ||
                        semesterName != null && semesterName.toLowerCase().contains(keyword)) {
                    tableModel.addRow(new Object[] {
                            stt++,
                            transcript.getId(),
                            studentCode,
                            studentName,
                            semesterName,
                            "Xem, Sửa, Xóa"
                    });
                }
            }
        } catch (Exception e) {
            log.error("Error searching transcripts", e);
            showError("Lỗi khi tìm kiếm bảng điểm");
        }
    }

    private void openAddTranscriptForm() {
        AddTranscriptForm.showDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                this,
                restTemplate,
                transcriptRepository,
                userRepository,
                semesterRepository);
    }

    private void viewTranscript(int row) {
        String transcriptId = (String) tableModel.getValueAt(row, 1);
        Transcript transcript = transcriptRepository.findById(transcriptId).orElse(null);
        if (transcript != null) {
            TranscriptDetail.showDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    transcript,
                    restTemplate,
                    transcriptRepository,
                    userRepository,
                    semesterRepository,
                    courseRepository,
                    gradeRepository);
        }
    }

    private void editTranscript(int row) {
        String transcriptId = (String) tableModel.getValueAt(row, 1);
        Transcript transcript = transcriptRepository.findById(transcriptId).orElse(null);
        if (transcript != null) {
            EditTranscriptForm.showDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    transcript,
                    this,
                    restTemplate,
                    transcriptRepository,
                    userRepository,
                    semesterRepository);
        }
    }

    private void deleteTranscript(int row) {
        String transcriptId = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa bảng điểm này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ResponseEntity<ApiResponse> response = restTemplate.exchange(
                        "/api/transcripts/" + transcriptId,
                        org.springframework.http.HttpMethod.DELETE,
                        null,
                        ApiResponse.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    showSuccess("Xóa bảng điểm thành công");
                    loadData();
                } else {
                    showError("Xóa bảng điểm thất bại");
                }
            } catch (Exception e) {
                log.error("Error deleting transcript", e);
                showError("Lỗi khi xóa bảng điểm");
            }
        }
    }

    private String getStudentNameById(String studentId) {
        return userRepository.findById(studentId)
                .map(User::getFullName)
                .orElse(null);
    }

    private String getStudentCodeById(String studentId) {
        return userRepository.findById(studentId)
                .map(User::getMsv)
                .orElse(null);
    }

    private String getSemesterNameById(String semesterId) {
        return semesterRepository.findById(semesterId)
                .map(semester -> semester.getSemester() + " - " + semester.getGroup() + " - Năm học: "
                        + semester.getYear())
                .orElse(null);
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

    private class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton viewButton;
        private JButton editButton;
        private JButton deleteButton;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            viewButton = createStyledButton("Xem", new Color(88, 86, 214));
            editButton = createStyledButton("Sửa", new Color(88, 86, 214));
            deleteButton = createStyledButton("Xóa", new Color(220, 53, 69));

            add(viewButton);
            add(editButton);
            add(deleteButton);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            return (java.awt.Component) this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton viewButton;
        private JButton editButton;
        private JButton deleteButton;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            viewButton = createStyledButton("Xem", new Color(88, 86, 214));
            editButton = createStyledButton("Sửa", new Color(88, 86, 214));
            deleteButton = createStyledButton("Xóa", new Color(220, 53, 69));

            panel.add(viewButton);
            panel.add(editButton);
            panel.add(deleteButton);

            viewButton.addActionListener(e -> {
                fireEditingStopped();
                viewTranscript(transcriptTable.getSelectedRow());
            });

            editButton.addActionListener(e -> {
                fireEditingStopped();
                editTranscript(transcriptTable.getSelectedRow());
            });

            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                deleteTranscript(transcriptTable.getSelectedRow());
            });
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }
            return (java.awt.Component) panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }
}