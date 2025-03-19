package com.study.java.studentmanagement.swing.course;

import com.study.java.studentmanagement.model.Course;
import com.study.java.studentmanagement.model.Major;
import com.study.java.studentmanagement.model.Teacher;
import com.study.java.studentmanagement.repository.CourseRepository;
import com.study.java.studentmanagement.repository.MajorRepository;
import com.study.java.studentmanagement.repository.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

@Slf4j
public class CoursePanel extends JPanel {
    private final CourseRepository courseRepository;
    private final MajorRepository majorRepository;
    private final TeacherRepository teacherRepository;

    private JTextField txtSearch;
    private JComboBox<String> cboSearchBy;
    private JButton btnSearch;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JTable tblCourses;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private JComboBox<Integer> cboPageSize;
    private JSpinner spnPage;
    private int totalPages;
    private static final String[] SEARCH_OPTIONS = { "Code", "Name", "Teacher", "Major" };
    private static final String[] TABLE_HEADERS = { "Code", "Name", "Credits", "Major", "Teacher", "Description" };

    public CoursePanel(CourseRepository courseRepository,
            MajorRepository majorRepository,
            TeacherRepository teacherRepository) {
        this.courseRepository = courseRepository;
        this.majorRepository = majorRepository;
        this.teacherRepository = teacherRepository;
        initComponents();
        loadData(0, 10);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Panel - Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtSearch = new JTextField(20);
        cboSearchBy = new JComboBox<>(SEARCH_OPTIONS);
        btnSearch = new JButton("Search");
        btnRefresh = new JButton("Refresh");

        searchPanel.add(new JLabel("Search by:"));
        searchPanel.add(cboSearchBy);
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnRefresh);

        // Top Panel - Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnAdd = new JButton("Add Course");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Delete");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        // Combine search and button panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Center Panel - Table
        tableModel = new DefaultTableModel(TABLE_HEADERS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCourses = new JTable(tableModel);
        tblCourses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCourses.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(tblCourses);

        // Bottom Panel - Pagination
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        lblTotal = new JLabel("Total: 0 courses");
        cboPageSize = new JComboBox<>(new Integer[] { 5, 10, 20, 50 });
        spnPage = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));

        bottomPanel.add(new JLabel("Page size:"));
        bottomPanel.add(cboPageSize);
        bottomPanel.add(new JLabel("Page:"));
        bottomPanel.add(spnPage);
        bottomPanel.add(lblTotal);

        // Add all panels to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Setup listeners
        setupListeners();
    }

    private void setupListeners() {
        btnSearch.addActionListener(e -> handleSearch());
        btnRefresh.addActionListener(e -> loadData(0, (Integer) cboPageSize.getSelectedItem()));
        btnAdd.addActionListener(e -> handleAdd());
        btnEdit.addActionListener(e -> handleEdit());
        btnDelete.addActionListener(e -> handleDelete());

        cboPageSize.addActionListener(e -> {
            int page = (Integer) spnPage.getValue() - 1;
            int size = (Integer) cboPageSize.getSelectedItem();
            loadData(page, size);
        });

        spnPage.addChangeListener(e -> {
            int page = (Integer) spnPage.getValue() - 1;
            int size = (Integer) cboPageSize.getSelectedItem();
            loadData(page, size);
        });
    }

    private void loadData(int page, int size) {
        try {
            Page<Course> coursePage = courseRepository.findAllActive(
                    PageRequest.of(page, size, Sort.by("code").ascending()));

            updateTable(coursePage.getContent());
            updatePagination(coursePage.getTotalElements(), coursePage.getTotalPages());
        } catch (Exception e) {
            log.error("Error loading courses", e);
            JOptionPane.showMessageDialog(this,
                    "Error loading courses: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearch() {
        String searchText = txtSearch.getText().trim();
        String searchBy = (String) cboSearchBy.getSelectedItem();
        int size = (Integer) cboPageSize.getSelectedItem();

        try {
            Page<Course> coursePage;
            switch (searchBy) {
                case "Code":
                    coursePage = courseRepository.findByCodeContainingIgnoreCase(
                            searchText,
                            PageRequest.of(0, size));
                    break;
                case "Name":
                    coursePage = courseRepository.findByNameContainingIgnoreCase(
                            searchText,
                            PageRequest.of(0, size));
                    break;
                case "Teacher":
                    coursePage = courseRepository.findByTeacherNameContainingIgnoreCase(
                            searchText,
                            PageRequest.of(0, size));
                    break;
                case "Major":
                    coursePage = courseRepository.findByMajorNameContainingIgnoreCase(
                            searchText,
                            PageRequest.of(0, size));
                    break;
                default:
                    return;
            }

            updateTable(coursePage.getContent());
            updatePagination(coursePage.getTotalElements(), coursePage.getTotalPages());
        } catch (Exception e) {
            log.error("Error searching courses", e);
            JOptionPane.showMessageDialog(this,
                    "Error searching courses: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAdd() {
        try {
            List<Major> majors = majorRepository.findAllActive();
            List<Teacher> teachers = teacherRepository.findAllActive();

            if (majors.isEmpty() || teachers.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please add majors and teachers first",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Course newCourse = AddCourseForm.showDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    majors,
                    teachers);

            if (newCourse != null) {
                courseRepository.save(newCourse);
                loadData(0, (Integer) cboPageSize.getSelectedItem());
                JOptionPane.showMessageDialog(this,
                        "Course added successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            log.error("Error adding course", e);
            JOptionPane.showMessageDialog(this,
                    "Error adding course: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEdit() {
        int selectedRow = tblCourses.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to edit",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String code = (String) tableModel.getValueAt(selectedRow, 0);
            Course course = courseRepository.findByCode(code)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            List<Major> majors = majorRepository.findAllActive();
            List<Teacher> teachers = teacherRepository.findAllActive();

            if (majors.isEmpty() || teachers.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please add majors and teachers first",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Course updatedCourse = UpdateCourseForm.showDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    course,
                    majors,
                    teachers);

            if (updatedCourse != null) {
                courseRepository.save(updatedCourse);
                loadData(0, (Integer) cboPageSize.getSelectedItem());
                JOptionPane.showMessageDialog(this,
                        "Course updated successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            log.error("Error editing course", e);
            JOptionPane.showMessageDialog(this,
                    "Error editing course: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int selectedRow = tblCourses.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to delete",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String code = (String) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this course?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                courseRepository.softDelete(code);
                loadData(0, (Integer) cboPageSize.getSelectedItem());
                JOptionPane.showMessageDialog(this,
                        "Course deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            log.error("Error deleting course", e);
            JOptionPane.showMessageDialog(this,
                    "Error deleting course: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Course> courses) {
        tableModel.setRowCount(0);
        for (Course course : courses) {
            Vector<Object> row = new Vector<>();
            row.add(course.getCode());
            row.add(course.getName());
            row.add(course.getCredits());
            row.add(course.getMajor().getName());
            row.add(course.getTeacher().getName());
            row.add(course.getDescription());
            tableModel.addRow(row);
        }
    }

    private void updatePagination(long totalElements, int totalPages) {
        this.totalPages = totalPages;
        lblTotal.setText(String.format("Total: %d courses", totalElements));

        // Update spinner model
        SpinnerNumberModel model = (SpinnerNumberModel) spnPage.getModel();
        model.setMaximum(Math.max(1, totalPages));
    }
}