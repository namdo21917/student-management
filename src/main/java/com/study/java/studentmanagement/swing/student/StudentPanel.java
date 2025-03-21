package com.study.java.studentmanagement.swing.student;

import com.formdev.flatlaf.FlatClientProperties;
import com.study.java.studentmanagement.dto.user.UserResponse;
import com.study.java.studentmanagement.model.Major;
import com.study.java.studentmanagement.model.Teacher;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.MajorRepository;
import com.study.java.studentmanagement.repository.TeacherRepository;
import com.study.java.studentmanagement.repository.UserRepository;
import com.study.java.studentmanagement.service.ApiService;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StudentPanel extends JPanel {
    private final ApiService apiService;
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

    public StudentPanel(ApiService apiService, UserRepository userRepository,
            TeacherRepository teacherRepository, MajorRepository majorRepository) {
        this.apiService = apiService;
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.majorRepository = majorRepository;

        initializeUI();
//        loadData();
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
            // Lấy danh sách sinh viên từ API
            ApiResponse<List<UserResponse>> response = apiService.get(
                    "/api/user/getAll",
                    new ParameterizedTypeReference<ApiResponse<List<UserResponse>>>() {
                    });

            if (response != null) {
                List<UserResponse> userResponses = response.getData();
                students = convertToUsers(userResponses);
                majors = majorRepository.findAll();
                teachers = teacherRepository.findAll();
                studentsTableModel.setStudents(students);
            }
        } catch (Exception e) {
            log.error("Error loading data", e);
            showError("Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) {
            try {
                ApiResponse<List<UserResponse>> response = apiService.post(
                        "/api/user/searchStudents?keyword=" + keyword,
                        null,
                        new ParameterizedTypeReference<ApiResponse<List<UserResponse>>>() {
                        });

                if (response != null) {
                    List<UserResponse> userResponses = response.getData();
                    students = convertToUsers(userResponses);
                    studentsTableModel.setStudents(students);
                    if (students.isEmpty()) {
                        showError("Không tìm thấy sinh viên");
                    }
                }
            } catch (Exception e) {
                log.error("Error searching students", e);
                showError("Lỗi khi tìm kiếm sinh viên: " + e.getMessage());
            }
        } else {
            loadData();
        }
    }

    private List<User> convertToUsers(List<UserResponse> userResponses) {
        // Collect all majorIds first
        List<String> majorIds = userResponses.stream()
                .map(UserResponse::getMajorId)
                .distinct()
                .toList();

        // Fetch all majors in one query
        List<Major> majors = majorRepository.findAllById(majorIds);

        // Create a map for quick lookup
        Map<String, Major> majorMap = majors.stream()
                .collect(Collectors.toMap(Major::getId, major -> major));

        List<User> users = new ArrayList<>();
        for (UserResponse response : userResponses) {
            User user = new User();
            user.setId(response.getId());
            user.setFullName(response.getFullName());
            user.setMsv(response.getMsv());
            user.setEmail(response.getEmail());
            user.setGvcn(response.getGvcn());

            // Get major from map instead of querying repository
            Major major = majorMap.get(response.getMajorId());
            user.setMajor(major);
            if (major != null) {
                user.setMajorName(major.getName());
            }

            user.setClassName(response.getClassName());
            user.setDeleted(response.isDeleted());
            users.add(user);
        }
        return users;
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
                apiService,
                userRepository,
                teacherRepository,
                majorRepository);
    }

    private void handleDelete(User student) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa sinh viên này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            try {
                ApiResponse<Void> response = apiService.delete(
                        "/api/user/delete/" + student.getId(),
                        new ParameterizedTypeReference<ApiResponse<Void>>() {
                        });

                if (response != null) {
                    showSuccess("Xóa sinh viên thành công");
                    loadData();
                } else {
                    showError("Lỗi khi xóa sinh viên");
                }
            } catch (Exception e) {
                log.error("Error deleting student", e);
                showError("Lỗi khi xóa sinh viên: " + e.getMessage());
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
                apiService,
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

            String majorName = student.getMajor() != null ? student.getMajor().getName() : "";

            switch (columnIndex) {
                case 0:
                    return rowIndex + 1;
                case 1:
                    return student.getFullName();
                case 2:
                    return student.getMsv();
                case 3:
                    return student.getGvcn();
                case 4:
                    return majorName;
                case 5:
                    return student.getClassName();
                case 6:
                    return "Xem, Sửa, Xóa";
                default:
                    return null;
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

    private class ActionRenderer extends DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel("Tùy chọn");
            label.setHorizontalAlignment(JLabel.CENTER);
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                label.setBackground(table.getBackground());
                label.setForeground(table.getForeground());
            }
            label.setOpaque(true);
            return label;
        }
    }

    private class ActionEditor extends DefaultCellEditor {
        private User currentStudent;

        public ActionEditor() {
            super(new JTextField());
            setClickCountToStart(1);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentStudent = studentsTableModel.students.get(row);

            JPopupMenu popup = new JPopupMenu();

            JMenuItem viewItem = new JMenuItem("Xem chi tiết");
            viewItem.addActionListener(e -> {
                handleView(currentStudent);
                fireEditingStopped();
            });

            JMenuItem editItem = new JMenuItem("Sửa");
            editItem.addActionListener(e -> {
                handleEdit(currentStudent);
                fireEditingStopped();
            });

            JMenuItem deleteItem = new JMenuItem("Xóa");
            deleteItem.addActionListener(e -> {
                handleDelete(currentStudent);
                fireEditingStopped();
            });

            popup.add(viewItem);
            popup.add(editItem);
            popup.add(deleteItem);

            SwingUtilities.invokeLater(() -> {
                popup.show(table, table.getCellRect(row, column, true).x,
                        table.getCellRect(row, column, true).y + table.getRowHeight(row));
            });

            JLabel label = new JLabel("Tùy chọn");
            label.setHorizontalAlignment(JLabel.CENTER);
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                label.setBackground(table.getBackground());
                label.setForeground(table.getForeground());
            }
            label.setOpaque(true);
            return label;
        }

        @Override
        public Object getCellEditorValue() {
            return "Tùy chọn";
        }
    }
}