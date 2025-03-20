package com.study.java.studentmanagement.swing.teacher;

import com.formdev.flatlaf.FlatClientProperties;
import com.study.java.studentmanagement.model.Teacher;
import com.study.java.studentmanagement.repository.TeacherRepository;
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
public class TeacherPanel extends JPanel {
    private final RestTemplate restTemplate;
    private final TeacherRepository teacherRepository;

    private JButton addButton;
    private JButton refreshButton;
    private JButton searchButton;
    private JTextField searchField;
    private JTable teachersTable;
    private TeachersTableModel teachersTableModel;
    private List<Teacher> teachers;

    public TeacherPanel(RestTemplate restTemplate, TeacherRepository teacherRepository) {
        this.restTemplate = restTemplate;
        this.teacherRepository = teacherRepository;

        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Thông tin giáo viên", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        // Search field
        searchField = new JTextField(20);
        searchField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm giáo viên...");
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
        addButton = createStyledButton("Thêm giáo viên", new Color(40, 167, 69));
        addButton.addActionListener(e -> handleAdd());
        buttonPanel.add(addButton);

        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Create table
        teachersTableModel = new TeachersTableModel();
        teachersTable = new JTable(teachersTableModel);
        teachersTable.setFillsViewportHeight(true);
        teachersTable.setRowHeight(40);
        teachersTable.setBackground(Color.WHITE);
        teachersTable.setShowVerticalLines(true);
        teachersTable.setShowHorizontalLines(true);
        teachersTable.setGridColor(new Color(220, 220, 220));

        // Center align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < teachersTable.getColumnCount(); i++) {
            teachersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Style table header
        JTableHeader header = teachersTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK);

        // Set column widths
        teachersTable.getColumnModel().getColumn(0).setPreferredWidth(50); // STT
        teachersTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Mã GV
        teachersTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Họ tên
        teachersTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Hành động

        // Set action column renderer and editor
        teachersTable.getColumnModel().getColumn(3).setCellRenderer(new ActionRenderer());
        teachersTable.getColumnModel().getColumn(3).setCellEditor(new ActionEditor());

        // Add scroll pane
        JScrollPane scrollPane = new JScrollPane(teachersTable);
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
            teachers = teacherRepository.findAll();
            teachersTableModel.setTeachers(teachers);
        } catch (Exception e) {
            log.error("Error loading data", e);
            showError("Lỗi khi tải dữ liệu");
        }
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) {
            try {
                ResponseEntity<List<Teacher>> response = restTemplate.getForEntity(
                        "/api/teachers/search?keyword=" + keyword,
                        List.class);
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    teachers = response.getBody();
                    teachersTableModel.setTeachers(teachers);
                    if (teachers.isEmpty()) {
                        showError("Không tìm thấy giáo viên");
                    }
                }
            } catch (Exception e) {
                log.error("Error searching teachers", e);
                showError("Lỗi khi tìm kiếm giáo viên");
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
        AddTeacherForm.showDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                this,
                restTemplate,
                teacherRepository);
    }

    private void handleDelete(Teacher teacher) {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa giáo viên " + teacher.getFullName() + " không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            try {
                ResponseEntity<ApiResponse> response = restTemplate.exchange(
                        "/api/teachers/" + teacher.getId(),
                        org.springframework.http.HttpMethod.DELETE,
                        null,
                        ApiResponse.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    showSuccess("Xóa giáo viên thành công");
                    loadData();
                } else {
                    showError("Xóa giáo viên thất bại");
                }
            } catch (Exception e) {
                log.error("Error deleting teacher", e);
                showError("Lỗi khi xóa giáo viên");
            }
        }
    }

    private void handleView(Teacher teacher) {
        TeacherDetail.showDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                teacher);
    }

    private void handleEdit(Teacher teacher) {
        UpdateTeacherForm.showDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                teacher,
                this,
                restTemplate,
                teacherRepository);
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

    private class TeachersTableModel extends AbstractTableModel {
        private final String[] columnNames = { "STT", "Mã GV", "Họ tên", "Hành động" };
        private List<Teacher> teachers = new ArrayList<>();

        public void setTeachers(List<Teacher> teachers) {
            this.teachers = teachers;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return teachers.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Teacher teacher = teachers.get(rowIndex);

            switch (columnIndex) {
                case 0:
                    return rowIndex + 1;
                case 1:
                    return teacher.getMgv();
                case 2:
                    return teacher.getFullName();
                case 3:
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
            return columnIndex == 3;
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
        private Teacher currentTeacher;

        public ActionEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            editButton = createActionButton("Sửa");
            deleteButton = createActionButton("Xóa");
            viewButton = createActionButton("Xem chi tiết");

            editButton.addActionListener(e -> {
                handleEdit(currentTeacher);
                fireEditingStopped();
            });

            deleteButton.addActionListener(e -> {
                handleDelete(currentTeacher);
                fireEditingStopped();
            });

            viewButton.addActionListener(e -> {
                handleView(currentTeacher);
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
            currentTeacher = teachersTableModel.teachers.get(row);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return panel;
        }
    }
}