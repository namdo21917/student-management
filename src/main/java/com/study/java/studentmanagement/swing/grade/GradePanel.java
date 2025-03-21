package com.study.java.studentmanagement.swing.grade;

import com.study.java.studentmanagement.model.Course;
import com.study.java.studentmanagement.model.Grade;
import com.study.java.studentmanagement.model.Semester;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.repository.CourseRepository;
import com.study.java.studentmanagement.repository.GradeRepository;
import com.study.java.studentmanagement.repository.SemesterRepository;
import com.study.java.studentmanagement.repository.UserRepository;
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
public class GradePanel extends JPanel {
    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;

    private JTextField txtSearch;
    private JComboBox<String> cboSearchBy;
    private JButton btnSearch;
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JTable tblGrades;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private JComboBox<Integer> cboPageSize;
    private JSpinner spnPage;
    private int totalPages;
    private static final String[] SEARCH_OPTIONS = {"Student", "Course", "Semester"};
    private static final String[] TABLE_HEADERS = {"Student", "Course", "Semester", "Midterm Score", "Final Score",
            "Average"};

    public GradePanel(GradeRepository gradeRepository,
                      UserRepository userRepository,
                      CourseRepository courseRepository,
                      SemesterRepository semesterRepository) {
        this.gradeRepository = gradeRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.semesterRepository = semesterRepository;
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
        btnAdd = new JButton("Add Grade");
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
        tblGrades = new JTable(tableModel);
        tblGrades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblGrades.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(tblGrades);

        // Bottom Panel - Pagination
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        lblTotal = new JLabel("Total: 0 grades");
        cboPageSize = new JComboBox<>(new Integer[]{5, 10, 20, 50});
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

    protected void loadData(int page, int size) {
        try {
            Page<Grade> gradePage = gradeRepository.findAllActive(
                    PageRequest.of(page, size, Sort.by("student.name").ascending()));

            updateTable(gradePage.getContent());
            updatePagination(gradePage.getTotalElements(), gradePage.getTotalPages());
        } catch (Exception e) {
            log.error("Error loading grades", e);
            JOptionPane.showMessageDialog(this,
                    "Error loading grades: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearch() {
        String searchText = txtSearch.getText().trim();
        String searchBy = (String) cboSearchBy.getSelectedItem();
        int size = (Integer) cboPageSize.getSelectedItem();

        try {
            Page<Grade> gradePage;
            switch (searchBy) {
                case "Student":
                    gradePage = gradeRepository.findByStudentNameContainingIgnoreCase(
                            searchText,
                            PageRequest.of(0, size));
                    break;
                case "Course":
                    gradePage = gradeRepository.findByCourseNameContainingIgnoreCase(
                            searchText,
                            PageRequest.of(0, size));
                    break;
                case "Semester":
                    gradePage = gradeRepository.findBySemesterNameContainingIgnoreCase(
                            searchText,
                            PageRequest.of(0, size));
                    break;
                default:
                    return;
            }

            updateTable(gradePage.getContent());
            updatePagination(gradePage.getTotalElements(), gradePage.getTotalPages());
        } catch (Exception e) {
            log.error("Error searching grades", e);
            JOptionPane.showMessageDialog(this,
                    "Error searching grades: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAdd() {
        try {
            List<User> students = userRepository.findAllStudents();
            List<Course> courses = courseRepository.findAllActive();
            List<Semester> semesters = semesterRepository.findAllByActive();

            if (students.isEmpty() || courses.isEmpty() || semesters.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please add students, courses and semesters first",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Grade newGrade = AddGradeForm.showDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    students,
                    courses,
                    semesters);

            if (newGrade != null) {
                gradeRepository.save(newGrade);
                loadData(0, (Integer) cboPageSize.getSelectedItem());
                JOptionPane.showMessageDialog(this,
                        "Grade added successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            log.error("Error adding grade", e);
            JOptionPane.showMessageDialog(this,
                    "Error adding grade: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEdit() {
        int selectedRow = tblGrades.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a grade to edit",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // TODO: Implement edit grade functionality
            JOptionPane.showMessageDialog(this,
                    "Edit functionality will be implemented soon",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            log.error("Error editing grade", e);
            JOptionPane.showMessageDialog(this,
                    "Error editing grade: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        int selectedRow = tblGrades.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a grade to delete",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Grade grade = getSelectedGrade();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this grade?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                gradeRepository.softDelete(grade.getId());
                loadData(0, (Integer) cboPageSize.getSelectedItem());
                JOptionPane.showMessageDialog(this,
                        "Grade deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            log.error("Error deleting grade", e);
            JOptionPane.showMessageDialog(this,
                    "Error deleting grade: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Grade getSelectedGrade() {
        int selectedRow = tblGrades.getSelectedRow();
        String studentName = (String) tableModel.getValueAt(selectedRow, 0);
        String courseName = (String) tableModel.getValueAt(selectedRow, 1);
        String semesterName = (String) tableModel.getValueAt(selectedRow, 2);

        return gradeRepository.findByStudentNameAndCourseNameAndSemesterName(
                        studentName, courseName, semesterName)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
    }

    private void updateTable(List<Grade> grades) {
        tableModel.setRowCount(0);
        for (Grade grade : grades) {
            Vector<Object> row = new Vector<>();
            row.add(grade.getStudent().getFullName());
            row.add(grade.getCourseName());
            row.add(grade.getSemester().getSemester());
            row.add(grade.getMidScore());
            row.add(grade.getFinalScore());
            row.add(calculateAverage(grade.getMidScore(), grade.getFinalScore()));
            tableModel.addRow(row);
        }
    }

    private double calculateAverage(double midtermScore, double finalScore) {
        return (midtermScore * 0.4) + (finalScore * 0.6);
    }

    private void updatePagination(long totalElements, int totalPages) {
        this.totalPages = totalPages;
        lblTotal.setText(String.format("Total: %d grades", totalElements));

        // Update spinner model
        SpinnerNumberModel model = (SpinnerNumberModel) spnPage.getModel();
        model.setMaximum(Math.max(1, totalPages));
    }
}