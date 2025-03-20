package com.study.java.studentmanagement.swing.major;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatLaf;
import com.study.java.studentmanagement.model.Major;
import com.study.java.studentmanagement.repository.MajorRepository;
import com.study.java.studentmanagement.util.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@Slf4j
public class MajorPanel extends JPanel {
    private final MajorRepository majorRepository;
    private final RestTemplate restTemplate;
    private JTable majorTable;
    private JTextField searchField;
    private JComboBox<String> searchCriteria;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JLabel totalLabel;
    private JLabel pageLabel;
    private JSpinner pageSpinner;
    private JSpinner pageSizeSpinner;
    private int currentPage = 0;
    private int pageSize = 10;
    private long totalElements = 0;
    private int totalPages = 0;

    public MajorPanel(MajorRepository majorRepository, RestTemplate restTemplate) {
        this.majorRepository = majorRepository;
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

        // Create search panel
        JPanel searchPanel = createSearchPanel();

        // Create table
        JPanel tablePanel = createTablePanel();

        // Create button panel
        JPanel buttonPanel = createButtonPanel();

        // Create pagination panel
        JPanel paginationPanel = createPaginationPanel();

        // Add all panels to the main panel
        add(searchPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(paginationPanel, BorderLayout.EAST);
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.putClientProperty(FlatClientProperties.TEXT_FIELD_PLACEHOLDER, "Tìm kiếm...");

        searchCriteria = new JComboBox<>(new String[] { "Mã ngành", "Tên ngành", "Mô tả" });
        searchCriteria.setPreferredSize(new Dimension(120, 35));

        JButton searchButton = createStyledButton("Tìm kiếm", new Color(88, 86, 214));
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.addActionListener(e -> handleSearch());

        refreshButton = createStyledButton("Làm mới", new Color(88, 86, 214));
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            currentPage = 0;
            loadData();
        });

        panel.add(new JLabel("Tìm kiếm theo:"));
        panel.add(searchCriteria);
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(refreshButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        String[] columns = { "Mã ngành", "Tên ngành", "Mô tả", "Trạng thái" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        majorTable = new JTable(model);
        majorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        majorTable.setRowHeight(30);
        majorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        majorTable.setFont(new Font("Arial", Font.PLAIN, 14));
        majorTable.setBackground(Color.WHITE);
        majorTable.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(majorTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        addButton = createStyledButton("Thêm ngành", new Color(40, 167, 69));
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.addActionListener(e -> handleAdd());

        editButton = createStyledButton("Sửa ngành", new Color(255, 193, 7));
        editButton.setPreferredSize(new Dimension(120, 35));
        editButton.addActionListener(e -> handleEdit());

        deleteButton = createStyledButton("Xóa ngành", new Color(220, 53, 69));
        deleteButton.setPreferredSize(new Dimension(120, 35));
        deleteButton.addActionListener(e -> handleDelete());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);

        return panel;
    }

    private JPanel createPaginationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        totalLabel = new JLabel("Tổng số: 0");
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        pageLabel = new JLabel("Trang: ");
        pageLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        pageSpinner.setPreferredSize(new Dimension(60, 25));
        pageSpinner.addChangeListener(e -> {
            currentPage = (int) pageSpinner.getValue() - 1;
            loadData();
        });

        JLabel pageSizeLabel = new JLabel("Số dòng: ");
        pageSizeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        pageSizeSpinner = new JSpinner(new SpinnerNumberModel(10, 5, 100, 5));
        pageSizeSpinner.setPreferredSize(new Dimension(60, 25));
        pageSizeSpinner.addChangeListener(e -> {
            pageSize = (int) pageSizeSpinner.getValue();
            currentPage = 0;
            loadData();
        });

        panel.add(totalLabel);
        panel.add(pageLabel);
        panel.add(pageSpinner);
        panel.add(pageSizeLabel);
        panel.add(pageSizeSpinner);

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

    private void handleSearch() {
        currentPage = 0;
        loadData();
    }

    private void handleAdd() {
        UpdateMajorForm.showDialog(this, null, majorRepository, restTemplate);
    }

    private void handleEdit() {
        int selectedRow = majorTable.getSelectedRow();
        if (selectedRow == -1) {
            showNotification("Vui lòng chọn ngành cần sửa", "error");
            return;
        }

        String majorCode = (String) majorTable.getValueAt(selectedRow, 0);
        try {
            Major major = majorRepository.findByCode(majorCode)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy ngành"));
            UpdateMajorForm.showDialog(this, major, majorRepository, restTemplate);
        } catch (Exception e) {
            log.error("Error finding major", e);
            showNotification("Lỗi khi tìm ngành: " + e.getMessage(), "error");
        }
    }

    private void handleDelete() {
        int selectedRow = majorTable.getSelectedRow();
        if (selectedRow == -1) {
            showNotification("Vui lòng chọn ngành cần xóa", "error");
            return;
        }

        String majorCode = (String) majorTable.getValueAt(selectedRow, 0);
        String majorName = (String) majorTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa ngành " + majorName + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                        "/api/majors/delete/" + majorCode,
                        null,
                        ApiResponse.class);

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null
                        && response.getBody().isSuccess()) {
                    showNotification("Xóa ngành thành công", "success");
                    loadData();
                } else {
                    showNotification("Xóa ngành thất bại", "error");
                }
            } catch (Exception e) {
                log.error("Error deleting major", e);
                showNotification("Lỗi khi xóa ngành: " + e.getMessage(), "error");
            }
        }
    }

    public void loadData() {
        try {
            String searchText = searchField.getText().trim();
            String searchBy = (String) searchCriteria.getSelectedItem();
            Page<Major> majorPage;

            if (!searchText.isEmpty()) {
                switch (searchBy) {
                    case "Mã ngành":
                        majorPage = majorRepository.findByCodeContainingIgnoreCase(searchText,
                                PageRequest.of(currentPage, pageSize));
                        break;
                    case "Tên ngành":
                        majorPage = majorRepository.findByNameContainingIgnoreCase(searchText,
                                PageRequest.of(currentPage, pageSize));
                        break;
                    case "Mô tả":
                        majorPage = majorRepository.findByDescriptionContainingIgnoreCase(searchText,
                                PageRequest.of(currentPage, pageSize));
                        break;
                    default:
                        majorPage = majorRepository.findAll(PageRequest.of(currentPage, pageSize));
                }
            } else {
                majorPage = majorRepository.findAll(PageRequest.of(currentPage, pageSize));
            }

            updateTable(majorPage.getContent());
            updatePagination(majorPage.getTotalElements(), majorPage.getTotalPages());

        } catch (Exception e) {
            log.error("Error loading majors", e);
            showNotification("Lỗi khi tải dữ liệu: " + e.getMessage(), "error");
        }
    }

    private void updateTable(List<Major> majors) {
        DefaultTableModel model = (DefaultTableModel) majorTable.getModel();
        model.setRowCount(0);

        for (Major major : majors) {
            model.addRow(new Object[] {
                    major.getCode(),
                    major.getName(),
                    major.getDescription(),
                    major.isActive() ? "Đang hoạt động" : "Đã khóa"
            });
        }
    }

    private void updatePagination(long totalElements, int totalPages) {
        this.totalElements = totalElements;
        this.totalPages = totalPages;

        totalLabel.setText("Tổng số: " + totalElements);
        pageSpinner.setModel(new SpinnerNumberModel(currentPage + 1, 1, totalPages, 1));
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