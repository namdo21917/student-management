package com.study.java.studentmanagement.swing.semester;

import com.formdev.flatlaf.FlatClientProperties;
import com.study.java.studentmanagement.model.Semester;
import com.study.java.studentmanagement.repository.SemesterRepository;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class SemesterPanel extends JPanel {
    private final SemesterRepository semesterRepository;
    private final RestTemplate restTemplate;
    private JTextField searchTextField;
    private JComboBox<String> searchComboBox;
    private JTable table;
    private DefaultTableModel model;
    private JSpinner pageSpinner;
    private JSpinner pageSizeSpinner;
    private JLabel totalPagesLabel;
    private JLabel totalItemsLabel;
    private int currentPage = 0;
    private int pageSize = 10;
    private String searchKeyword = "";
    private String searchFieldType = "all";

    public SemesterPanel(SemesterRepository semesterRepository, RestTemplate restTemplate) {
        this.semesterRepository = semesterRepository;
        this.restTemplate = restTemplate;
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Search Panel
        JPanel searchPanel = createSearchPanel();

        // Table Panel
        JPanel tablePanel = createTablePanel();

        // Pagination Panel
        JPanel paginationPanel = createPaginationPanel();

        // Button Panel
        JPanel buttonPanel = createButtonPanel();

        add(searchPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.EAST);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        searchTextField = new JTextField(20);
        searchTextField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm...");

        searchComboBox = new JComboBox<>(new String[] { "Tất cả", "Học kỳ", "Nhóm", "Năm học" });
        searchComboBox.setSelectedIndex(0);

        JButton searchButton = createStyledButton("Tìm kiếm", new Color(0, 123, 255));
        searchButton.addActionListener(e -> handleSearch());

        searchPanel.add(new JLabel("Tìm kiếm theo:"));
        searchPanel.add(searchComboBox);
        searchPanel.add(searchTextField);
        searchPanel.add(searchButton);

        return searchPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[] {
                "STT", "Học kỳ", "Nhóm", "Năm học", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"
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
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createPaginationPanel() {
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paginationPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel pageLabel = new JLabel("Trang:");
        pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        pageSpinner.addChangeListener(e -> handlePageChange());

        JLabel pageSizeLabel = new JLabel("Số dòng:");
        pageSizeSpinner = new JSpinner(new SpinnerNumberModel(10, 5, 50, 5));
        pageSizeSpinner.addChangeListener(e -> handlePageSizeChange());

        totalPagesLabel = new JLabel("Tổng số trang: 0");
        totalItemsLabel = new JLabel("Tổng số học kỳ: 0");

        paginationPanel.add(pageLabel);
        paginationPanel.add(pageSpinner);
        paginationPanel.add(Box.createHorizontalStrut(20));
        paginationPanel.add(pageSizeLabel);
        paginationPanel.add(pageSizeSpinner);
        paginationPanel.add(Box.createHorizontalStrut(20));
        paginationPanel.add(totalPagesLabel);
        paginationPanel.add(Box.createHorizontalStrut(20));
        paginationPanel.add(totalItemsLabel);

        return paginationPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        JButton addButton = createStyledButton("Thêm mới", new Color(40, 167, 69));
        JButton editButton = createStyledButton("Sửa", new Color(255, 193, 7));
        JButton deleteButton = createStyledButton("Xóa", new Color(220, 53, 69));
        JButton refreshButton = createStyledButton("Làm mới", new Color(108, 117, 125));

        addButton.addActionListener(e -> handleAdd());
        editButton.addActionListener(e -> handleEdit());
        deleteButton.addActionListener(e -> handleDelete());
        refreshButton.addActionListener(e -> handleRefresh());

        buttonPanel.add(addButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(refreshButton);

        return buttonPanel;
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

    private void loadData() {
        try {
            Page<Semester> page = semesterRepository.searchSemesters(searchKeyword,
                    PageRequest.of(currentPage, pageSize));
            updateTableData(page.getContent());
            updatePaginationInfo(page);
        } catch (Exception e) {
            log.error("Error loading semester data", e);
            showError("Lỗi khi tải dữ liệu học kỳ");
        }
    }

    private void updateTableData(List<Semester> semesters) {
        model.setRowCount(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 0; i < semesters.size(); i++) {
            Semester semester = semesters.get(i);
            model.addRow(new Object[] {
                    i + 1,
                    semester.getSemester(),
                    semester.getGroup(),
                    semester.getYear(),
                    semester.getStartDate() != null ? semester.getStartDate().format(dateFormatter) : "",
                    semester.getEndDate() != null ? semester.getEndDate().format(dateFormatter) : "",
                    semester.isActive() ? "Đang hoạt động" : "Đã khóa"
            });
        }
    }

    private void updatePaginationInfo(Page<Semester> page) {
        int totalPages = page.getTotalPages();
        long totalItems = page.getTotalElements();

        pageSpinner.setValue(currentPage + 1);
        ((SpinnerNumberModel) pageSpinner.getModel()).setMaximum(totalPages);
        totalPagesLabel.setText("Tổng số trang: " + totalPages);
        totalItemsLabel.setText("Tổng số học kỳ: " + totalItems);
    }

    private void handleSearch() {
        searchKeyword = searchTextField.getText().trim();
        searchFieldType = getSearchFieldFromComboBox();
        currentPage = 0;
        loadData();
    }

    private String getSearchFieldFromComboBox() {
        switch (searchComboBox.getSelectedIndex()) {
            case 1:
                return "semester";
            case 2:
                return "group";
            case 3:
                return "year";
            default:
                return "all";
        }
    }

    private void handlePageChange() {
        currentPage = (int) pageSpinner.getValue() - 1;
        loadData();
    }

    private void handlePageSizeChange() {
        pageSize = (int) pageSizeSpinner.getValue();
        currentPage = 0;
        loadData();
    }

    private void handleAdd() {
        // TODO: Implement add functionality
        showInfo("Chức năng thêm mới sẽ được cập nhật sau");
    }

    private void handleEdit() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn học kỳ cần sửa");
            return;
        }
        // TODO: Implement edit functionality
        showInfo("Chức năng sửa sẽ được cập nhật sau");
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Vui lòng chọn học kỳ cần xóa");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa học kỳ này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String semesterId = getSemesterIdFromRow(selectedRow);
                ResponseEntity<ApiResponse> response = restTemplate.exchange(
                        "/api/semesters/" + semesterId,
                        org.springframework.http.HttpMethod.DELETE,
                        null,
                        ApiResponse.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    showSuccess("Xóa học kỳ thành công");
                    loadData();
                } else {
                    showError("Lỗi khi xóa học kỳ");
                }
            } catch (Exception e) {
                log.error("Error deleting semester", e);
                showError("Lỗi khi xóa học kỳ");
            }
        }
    }

    private void handleRefresh() {
        searchTextField.setText("");
        searchComboBox.setSelectedIndex(0);
        currentPage = 0;
        pageSize = 10;
        loadData();
    }

    private String getSemesterIdFromRow(int row) {
        // TODO: Implement getting semester ID from the selected row
        return "";
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Thông tin",
                JOptionPane.INFORMATION_MESSAGE);
    }
}