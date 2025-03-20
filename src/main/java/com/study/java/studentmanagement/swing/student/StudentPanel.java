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
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class StudentPanel extends JPanel {
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final MajorRepository majorRepository;

    private JButton addButton;
    private JButton refreshButton;
    private JButton searchButton;
    private JTextField searchField;
    private JTable studentsTable;
    private StudentsTableModel studentsTableModel;
    private List<User> students;
    private List<Major> majors;
    private List<Teacher> teachers;

    public StudentPanel(RestTemplate restTemplate, UserRepository userRepository,
            TeacherRepository teacherRepository, MajorRepository majorRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.majorRepository = majorRepository;

        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Thông tin sinh viên", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        // Search field
        searchField = new JTextField(20);
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm sinh viên...");
        buttonPanel.add(searchField);

        // Search button
        searchButton = createStyledButton("Tìm kiếm", new Color(88, 86, 214));
        searchButton.addActionListener(e -> handleSearch());
        buttonPanel.add(searchButton);

        // Refresh button
        refreshButton = createStyledButton("Làm mới", new Color(88, 86, 214));
        refreshButton.addActionListener(e -> handleRefresh());
        buttonPanel.add(refreshButton);

        // Add button
        addButton = createStyledButton("Thêm sinh viên", new Color(40, 167, 69));
        addButton.addActionListener(e -> handleAdd());
        buttonPanel.add(addButton);

        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Create table
        studentsTableModel = new StudentsTableModel();
        studentsTable = new JTable(studentsTableModel);
        studentsTable.setFillsViewportHeight(true);
        studentsTable.setRowHeight(40);
        studentsTable.setBackground(Color.WHITE);
        studentsTable.setShowVerticalLines(true);
        studentsTable.setShowHorizontalLines(true);
        studentsTable.setGridColor(new Color(220, 220, 220));

        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < studentsTable.getColumnCount(); i++) {
            studentsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Style table header
        JTableHeader header = studentsTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK);

        // Set column widths
        studentsTable.getColumnModel().getColumn(0).setPreferredWidth(50); // STT
        studentsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên
        studentsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Mã sinh viên
        studentsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // GVCN
        studentsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Chuyên ngành
        studentsTable.getColumnModel().getColumn(5).setPreferredWidth(80); // Lớp
        studentsTable.getColumnModel().getColumn(6).setPreferredWidth(200); // Hành động

        // Set action column renderer and editor
        studentsTable.getColumnModel().getColumn(6).setCellRenderer(new ActionRenderer());
        studentsTable.getColumnModel().getColumn(6).setCellEditor(new ActionEditor());

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
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
            students = userRepository.findAll();
            majors = majorRepository.findAll();
            teachers = teacherRepository.findAll();
            studentsTableModel.setStudents(students);
        } catch (Exception e) {
            log.error("Error loading data", e);
            showError("Lỗi khi tải dữ liệu");
        }
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) {
            try {
                ResponseEntity<List<User>> response = restTemplate.getForEntity(
                        "/api/users/search?keyword=" + keyword,
                        List.class);
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    students = response.getBody();
                    studentsTableModel.setStudents(students);
                    if (students.isEmpty()) {
                        showError("Không tìm thấy sinh viên");
                    }
                }
            } catch (Exception e) {
                log.error("Error searching students", e);
                showError("Lỗi khi tìm kiếm sinh viên");
            }
        } else {
            loadData();
        }
    }

    private void handleRefresh() {
        searchField.setText("");
        loadData();
        showSuccess("Làm mới dữ liệu thành công");
    }

    private void handleAdd() {
        AddStudentForm.showDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                this,
                restTemplate,
                userRepository,
                teacherRepository,
                majorRepository);
    }

    private void handleDelete(User student) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa sinh viên " + student.getFullName() + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            try {
                ResponseEntity<ApiResponse> response = restTemplate.exchange(
                        "/api/users/" + student.getId(),
                        org.springframework.http.HttpMethod.DELETE,
                        null,
                        ApiResponse.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    showSuccess("Xóa sinh viên thành công");
                    loadData();
                } else {
                    showError("Xóa sinh viên thất bại");
                }
            } catch (Exception e) {
                log.error("Error deleting student", e);
                showError("Lỗi khi xóa sinh viên");
            }
        }
    }

    private void handleView(User student) {
        StudentDetail.showDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                student,
                teacherRepository,
                majorRepository);
    }

    private void handleEdit(User student) {
        UpdateStudentForm.showDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                student,
                this,
                restTemplate,
                userRepository,
                teacherRepository,
                majorRepository);
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

    private class StudentsTableModel extends AbstractTableModel {
        private final String[] columnNames = { "STT", "Tên", "Mã sinh viên", "GVCN", "Chuyên ngành", "Lớp",
                "Hành động" };
        private List<User> students = new ArrayList<>();

        public void setStudents(List<User> students) {
            this.students = students;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return students.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            User student = students.get(rowIndex);

            String majorName = majors.stream()
                    .filter(major -> major.getId().equals(student.getMajorId()))
                    .map(Major::getName)
                    .findFirst()
                    .orElse("");

            String gvcnName = teachers.stream()
                    .filter(teacher -> teacher.getId().equals(student.getGvcn()))
                    .map(Teacher::getFullName)
                    .findFirst()
                    .orElse("");

            switch (columnIndex) {
                case 0:
                    return rowIndex + 1;
                case 1:
                    return student.getFullName();
                case 2:
                    return student.getMsv();
                case 3:
                    return gvcnName;
                case 4:
                    return majorName;
                case 5:
                    return student.getClassName();
                case 6:
                    return "Hành động";
                default:
                    throw new IllegalArgumentException("Invalid column index");
            }
        }

        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 6;
        }
    }

    private class ActionRenderer extends JPanel implements TableCellRenderer {
        public ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            JButton viewButton = createActionButton("Xem chi tiết");
            add(viewButton);
        }

        private JButton createActionButton(String text) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setFocusPainted(false);
            button.setOpaque(true);
            button.setBorderPainted(true);
            button.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            return button;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            return this;
        }
    }

    private class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final JButton editButton;
        private final JButton deleteButton;
        private final JButton viewButton;
        private User currentStudent;

        public ActionEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            editButton = createActionButton("Sửa");
            deleteButton = createActionButton("Xóa");
            viewButton = createActionButton("Xem chi tiết");

            editButton.addActionListener(e -> {
                handleEdit(currentStudent);
                fireEditingStopped();
            });

            deleteButton.addActionListener(e -> {
                handleDelete(currentStudent);
                fireEditingStopped();
            });

            viewButton.addActionListener(e -> {
                handleView(currentStudent);
                fireEditingStopped();
            });

            panel.add(editButton);
            panel.add(deleteButton);
            panel.add(viewButton);
        }

        private JButton createActionButton(String text) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setFocusPainted(false);
            button.setOpaque(true);
            button.setBorderPainted(true);
            button.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentStudent = studentsTableModel.students.get(row);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return panel;
        }
    }
}