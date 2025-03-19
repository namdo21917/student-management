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

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Slf4j
public class AdminTeacherGradesPanel extends GradePanel {
    private final User currentUser;
    private JComboBox<Course> cboCourse;
    private JComboBox<Semester> cboSemester;
    private JButton btnExport;
    private JButton btnImport;
    private JButton btnStatistics;

    public AdminTeacherGradesPanel(
            GradeRepository gradeRepository,
            UserRepository userRepository,
            CourseRepository courseRepository,
            SemesterRepository semesterRepository,
            User currentUser) {
        super(gradeRepository, userRepository, courseRepository, semesterRepository);
        this.currentUser = currentUser;
        addAdditionalControls();
        setupAdditionalListeners();
        loadUserSpecificData();
    }

    private void addAdditionalControls() {
        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("Course:"));
        cboCourse = new JComboBox<>();
        filterPanel.add(cboCourse);

        filterPanel.add(new JLabel("Semester:"));
        cboSemester = new JComboBox<>();
        filterPanel.add(cboSemester);

        // Create additional buttons panel
        JPanel additionalButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnExport = new JButton("Export");
        btnImport = new JButton("Import");
        btnStatistics = new JButton("Statistics");

        additionalButtonPanel.add(btnExport);
        additionalButtonPanel.add(btnImport);
        additionalButtonPanel.add(btnStatistics);

        // Add panels to the top
        JPanel topPanel = (JPanel) getComponent(0);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        topPanel.add(additionalButtonPanel, BorderLayout.SOUTH);
    }

    private void setupAdditionalListeners() {
        cboCourse.addActionListener(e -> handleFilter());
        cboSemester.addActionListener(e -> handleFilter());

        btnExport.addActionListener(e -> handleExport());
        btnImport.addActionListener(e -> handleImport());
        btnStatistics.addActionListener(e -> handleStatistics());
    }

    private void loadUserSpecificData() {
        try {
            List<Course> courses;
            if (currentUser.isAdmin()) {
                courses = getCourseRepository().findAllActive();
            } else {
                courses = getCourseRepository().findByTeacherId(currentUser.getId());
            }
            courses.forEach(cboCourse::addItem);

            List<Semester> semesters = getSemesterRepository().findAllActive();
            semesters.forEach(cboSemester::addItem);

        } catch (Exception e) {
            log.error("Error loading user specific data", e);
            JOptionPane.showMessageDialog(this,
                    "Error loading data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleFilter() {
        Course selectedCourse = (Course) cboCourse.getSelectedItem();
        Semester selectedSemester = (Semester) cboSemester.getSelectedItem();

        if (selectedCourse == null || selectedSemester == null) {
            return;
        }

        try {
            Page<Grade> gradePage = getGradeRepository().findByCourseAndSemester(
                    selectedCourse.getId(),
                    selectedSemester.getId(),
                    PageRequest.of(0, getPageSize()));

            updateTable(gradePage.getContent());
            updatePagination(gradePage.getTotalElements(), gradePage.getTotalPages());

        } catch (Exception e) {
            log.error("Error filtering grades", e);
            JOptionPane.showMessageDialog(this,
                    "Error filtering grades: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleExport() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Grades");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                // TODO: Implement export functionality
                JOptionPane.showMessageDialog(this,
                        "Export functionality will be implemented soon",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            log.error("Error exporting grades", e);
            JOptionPane.showMessageDialog(this,
                    "Error exporting grades: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleImport() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Import Grades");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                // TODO: Implement import functionality
                JOptionPane.showMessageDialog(this,
                        "Import functionality will be implemented soon",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            log.error("Error importing grades", e);
            JOptionPane.showMessageDialog(this,
                    "Error importing grades: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleStatistics() {
        try {
            Course selectedCourse = (Course) cboCourse.getSelectedItem();
            Semester selectedSemester = (Semester) cboSemester.getSelectedItem();

            if (selectedCourse == null || selectedSemester == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a course and semester",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Calculate statistics
            double averageScore = getGradeRepository().calculateAverageScoreByCourseAndSemester(
                    selectedCourse.getId(),
                    selectedSemester.getId());

            long totalStudents = getGradeRepository().countByCourseAndSemester(
                    selectedCourse.getId(),
                    selectedSemester.getId());

            long passedStudents = getGradeRepository().countPassedStudentsByCourseAndSemester(
                    selectedCourse.getId(),
                    selectedSemester.getId());

            // Display statistics
            String message = String.format("""
                    Course: %s
                    Semester: %s
                    Total Students: %d
                    Passed Students: %d (%.1f%%)
                    Average Score: %.2f
                    """,
                    selectedCourse.getName(),
                    selectedSemester.getName(),
                    totalStudents,
                    passedStudents,
                    (totalStudents > 0 ? (passedStudents * 100.0 / totalStudents) : 0),
                    averageScore);

            JOptionPane.showMessageDialog(this,
                    message,
                    "Grade Statistics",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            log.error("Error calculating statistics", e);
            JOptionPane.showMessageDialog(this,
                    "Error calculating statistics: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void loadData(int page, int size) {
        Course selectedCourse = (Course) cboCourse.getSelectedItem();
        Semester selectedSemester = (Semester) cboSemester.getSelectedItem();

        try {
            Page<Grade> gradePage;
            if (selectedCourse != null && selectedSemester != null) {
                gradePage = getGradeRepository().findByCourseAndSemester(
                        selectedCourse.getId(),
                        selectedSemester.getId(),
                        PageRequest.of(page, size));
            } else if (!currentUser.isAdmin()) {
                gradePage = getGradeRepository().findByTeacherId(
                        currentUser.getId(),
                        PageRequest.of(page, size));
            } else {
                super.loadData(page, size);
                return;
            }

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
}