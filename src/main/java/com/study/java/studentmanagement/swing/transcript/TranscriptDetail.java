package com.study.java.studentmanagement.swing.transcript;

import com.formdev.flatlaf.FlatClientProperties;
import com.study.java.studentmanagement.model.Course;
import com.study.java.studentmanagement.model.Grade;
import com.study.java.studentmanagement.model.Semester;
import com.study.java.studentmanagement.model.Transcript;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.*;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Component
public class TranscriptDetail extends JDialog {
    private final RestTemplate restTemplate;
    private final TranscriptRepository transcriptRepository;
    private final UserRepository userRepository;
    private final SemesterRepository semesterRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;
    private final Transcript transcript;

    private JTable gradeTable;
    private DefaultTableModel tableModel;

    public TranscriptDetail(JFrame parent, Transcript transcript,
            RestTemplate restTemplate,
            TranscriptRepository transcriptRepository,
            UserRepository userRepository,
            SemesterRepository semesterRepository,
            CourseRepository courseRepository,
            GradeRepository gradeRepository) {
        super(parent, "Chi tiết bảng điểm", true);
        this.transcript = transcript;
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
        setSize(800, 600);
        setLocationRelativeTo(getParent());

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        User student = userRepository.findById(transcript.getStudentId()).orElse(null);
        String title = String.format("Bảng điểm sinh viên %s - %s",
                student != null ? student.getFullName() : "Không xác định",
                student != null ? student.getMsv() : "Không xác định");
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JButton addButton = createStyledButton("Thêm điểm", new Color(40, 167, 69));
        addButton.addActionListener(e -> showAddGradeForm());
        titlePanel.add(addButton, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = { "STT", "Mã môn", "Tên môn", "Số TC", "Điểm giữa kỳ", "Điểm cuối kỳ", "Điểm tổng kết",
                "Trạng thái", "Thao tác" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only allow editing of action column
            }
        };

        gradeTable = new JTable(tableModel);
        gradeTable.setRowHeight(40);
        gradeTable.setIntercellSpacing(new Dimension(0, 1));
        gradeTable.setGridColor(new Color(220, 220, 220));

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < gradeTable.getColumnCount(); i++) {
            gradeTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set up action column
        gradeTable.getColumn("Thao tác").setCellRenderer(new ButtonRenderer());
        gradeTable.getColumn("Thao tác").setCellEditor(new ButtonEditor(new JCheckBox()));

        add(new JScrollPane(gradeTable), BorderLayout.CENTER);
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
            List<Grade> grades = gradeRepository.findByTranscriptId(transcript.getId());
            tableModel.setRowCount(0);
            int stt = 1;

            for (Grade grade : grades) {
                Course course = courseRepository.findById(grade.getCourseId()).orElse(null);
                if (course != null) {
                    tableModel.addRow(new Object[] {
                            stt++,
                            course.getCode(),
                            course.getName(),
                            course.getCredit(),
                            grade.getMidScore(),
                            grade.getFinalScore(),
                            grade.getAverageScore(),
                            grade.getStatus(),
                            "Sửa, Xóa"
                    });
                }
            }
        } catch (Exception e) {
            log.error("Error loading grades", e);
            showError("Lỗi khi tải dữ liệu điểm");
        }
    }

    private void showAddGradeForm() {
        JDialog dialog = new JDialog(this, "Thêm điểm", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Course selection
        JComboBox<Course> courseComboBox = new JComboBox<>();
        courseRepository.findAll().forEach(courseComboBox::addItem);

        // Score fields
        JTextField midScoreField = new JTextField(10);
        JTextField finalScoreField = new JTextField(10);

        // Add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Môn học:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        dialog.add(courseComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Điểm giữa kỳ:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        dialog.add(midScoreField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Điểm cuối kỳ:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        dialog.add(finalScoreField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = createStyledButton("Lưu", new Color(40, 167, 69));
        JButton cancelButton = createStyledButton("Hủy", new Color(220, 53, 69));

        saveButton.addActionListener(e -> {
            try {
                Course selectedCourse = (Course) courseComboBox.getSelectedItem();
                double midScore = Double.parseDouble(midScoreField.getText());
                double finalScore = Double.parseDouble(finalScoreField.getText());

                Grade newGrade = new Grade();
                newGrade.setTranscriptId(transcript.getId());
                newGrade.setCourseId(selectedCourse.getId());
                newGrade.setMidScore(midScore);
                newGrade.setFinalScore(finalScore);

                // Calculate average score
                double averageScore = (midScore * 0.3) + (finalScore * 0.7);
                BigDecimal bd = new BigDecimal(averageScore).setScale(2, RoundingMode.HALF_UP);
                newGrade.setAverageScore(bd.doubleValue());
                newGrade.setStatus(newGrade.getAverageScore() >= 4.0 ? "Pass" : "Fail");

                ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                        "/api/grades",
                        newGrade,
                        ApiResponse.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    showSuccess("Thêm điểm thành công");
                    loadData();
                    dialog.dispose();
                } else {
                    showError("Thêm điểm thất bại");
                }
            } catch (NumberFormatException ex) {
                showError("Điểm không hợp lệ");
            } catch (Exception ex) {
                log.error("Error adding grade", ex);
                showError("Lỗi khi thêm điểm");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editGrade(int row) {
        String courseCode = (String) tableModel.getValueAt(row, 1);
        Course course = courseRepository.findByCode(courseCode).orElse(null);
        if (course == null)
            return;

        JDialog dialog = new JDialog(this, "Sửa điểm", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Score fields
        JTextField midScoreField = new JTextField(String.valueOf(tableModel.getValueAt(row, 4)), 10);
        JTextField finalScoreField = new JTextField(String.valueOf(tableModel.getValueAt(row, 5)), 10);

        // Add components
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Môn học: " + course.getName()), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Điểm giữa kỳ:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        dialog.add(midScoreField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Điểm cuối kỳ:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        dialog.add(finalScoreField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = createStyledButton("Lưu", new Color(40, 167, 69));
        JButton cancelButton = createStyledButton("Hủy", new Color(220, 53, 69));

        saveButton.addActionListener(e -> {
            try {
                double midScore = Double.parseDouble(midScoreField.getText());
                double finalScore = Double.parseDouble(finalScoreField.getText());

                Grade grade = gradeRepository.findByTranscriptIdAndCourseId(transcript.getId(), course.getId())
                        .orElse(null);
                if (grade != null) {
                    grade.setMidScore(midScore);
                    grade.setFinalScore(finalScore);

                    // Calculate average score
                    double averageScore = (midScore * 0.3) + (finalScore * 0.7);
                    BigDecimal bd = new BigDecimal(averageScore).setScale(2, RoundingMode.HALF_UP);
                    grade.setAverageScore(bd.doubleValue());
                    grade.setStatus(grade.getAverageScore() >= 4.0 ? "Pass" : "Fail");

                    ResponseEntity<ApiResponse> response = restTemplate.exchange(
                            "/api/grades/" + grade.getId(),
                            org.springframework.http.HttpMethod.PUT,
                            grade,
                            ApiResponse.class);

                    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                        showSuccess("Cập nhật điểm thành công");
                        loadData();
                        dialog.dispose();
                    } else {
                        showError("Cập nhật điểm thất bại");
                    }
                }
            } catch (NumberFormatException ex) {
                showError("Điểm không hợp lệ");
            } catch (Exception ex) {
                log.error("Error updating grade", ex);
                showError("Lỗi khi cập nhật điểm");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteGrade(int row) {
        String courseCode = (String) tableModel.getValueAt(row, 1);
        Course course = courseRepository.findByCode(courseCode).orElse(null);
        if (course == null)
            return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa điểm này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Grade grade = gradeRepository.findByTranscriptIdAndCourseId(transcript.getId(), course.getId())
                        .orElse(null);
                if (grade != null) {
                    ResponseEntity<ApiResponse> response = restTemplate.exchange(
                            "/api/grades/" + grade.getId(),
                            org.springframework.http.HttpMethod.DELETE,
                            null,
                            ApiResponse.class);

                    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                        showSuccess("Xóa điểm thành công");
                        loadData();
                    } else {
                        showError("Xóa điểm thất bại");
                    }
                }
            } catch (Exception e) {
                log.error("Error deleting grade", e);
                showError("Lỗi khi xóa điểm");
            }
        }
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
        private JButton editButton;
        private JButton deleteButton;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = createStyledButton("Sửa", new Color(88, 86, 214));
            deleteButton = createStyledButton("Xóa", new Color(220, 53, 69));

            add(editButton);
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(new Color(88, 86, 214));
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(Color.BLACK);
            }
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            editButton = createStyledButton("Sửa", new Color(88, 86, 214));
            deleteButton = createStyledButton("Xóa", new Color(220, 53, 69));

            panel.add(editButton);
            panel.add(deleteButton);

            editButton.addActionListener(e -> {
                fireEditingStopped();
                editGrade(gradeTable.getSelectedRow());
            });

            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                deleteGrade(gradeTable.getSelectedRow());
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (isSelected) {
                panel.setBackground(new Color(88, 86, 214));
            } else {
                panel.setBackground(Color.WHITE);
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    public static void showDialog(JFrame parent, Transcript transcript,
            RestTemplate restTemplate,
            TranscriptRepository transcriptRepository,
            UserRepository userRepository,
            SemesterRepository semesterRepository,
            CourseRepository courseRepository,
            GradeRepository gradeRepository) {
        TranscriptDetail dialog = new TranscriptDetail(
                parent, transcript, restTemplate, transcriptRepository,
                userRepository, semesterRepository, courseRepository, gradeRepository);
        dialog.setVisible(true);
    }
}