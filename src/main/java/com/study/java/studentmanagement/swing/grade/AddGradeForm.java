package com.study.java.studentmanagement.swing.grade;

import com.study.java.studentmanagement.model.Course;
import com.study.java.studentmanagement.model.Grade;
import com.study.java.studentmanagement.model.Semester;
import com.study.java.studentmanagement.model.User;
import com.study.java.studentmanagement.util.ValidationUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

@Slf4j
public class AddGradeForm extends JDialog {
    private JComboBox<User> cboStudent;
    private JComboBox<Course> cboCourse;
    private JComboBox<Semester> cboSemester;
    private JSpinner spnMidtermScore;
    private JSpinner spnFinalScore;
    private JButton btnSave;
    private JButton btnCancel;

    @Getter
    private Grade result = null;

    public AddGradeForm(Frame parent, List<User> students, List<Course> courses, List<Semester> semesters) {
        super(parent, "Add New Grade", true);
        initComponents(students, courses, semesters);
        setupListeners();
    }

    private void initComponents(List<User> students, List<Course> courses, List<Semester> semesters) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(400, 400);
        setLocationRelativeTo(getOwner());

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Grade Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Student
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Student:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cboStudent = new JComboBox<>(students.toArray(new User[0]));
        formPanel.add(cboStudent, gbc);

        // Course
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Course:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cboCourse = new JComboBox<>(courses.toArray(new Course[0]));
        formPanel.add(cboCourse, gbc);

        // Semester
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Semester:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cboSemester = new JComboBox<>(semesters.toArray(new Semester[0]));
        formPanel.add(cboSemester, gbc);

        // Midterm Score
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Midterm Score:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerNumberModel midtermModel = new SpinnerNumberModel(0.0, 0.0, 10.0, 0.1);
        spnMidtermScore = new JSpinner(midtermModel);
        formPanel.add(spnMidtermScore, gbc);

        // Final Score
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Final Score:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerNumberModel finalModel = new SpinnerNumberModel(0.0, 0.0, 10.0, 0.1);
        spnFinalScore = new JSpinner(finalModel);
        formPanel.add(spnFinalScore, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnSave = new JButton("Save");
        btnCancel = new JButton("Cancel");
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        // Add panels to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to dialog
        add(mainPanel);
    }

    private void setupListeners() {
        btnSave.addActionListener(e -> handleSave());
        btnCancel.addActionListener(e -> dispose());
    }

    private void handleSave() {
        // Get values from form
        User student = (User) cboStudent.getSelectedItem();
        Course course = (Course) cboCourse.getSelectedItem();
        Semester semester = (Semester) cboSemester.getSelectedItem();
        double midtermScore = (Double) spnMidtermScore.getValue();
        double finalScore = (Double) spnFinalScore.getValue();

        // Validate input
        if (!validateInput(student, course, semester)) {
            return;
        }

        // Create new grade
        Grade grade = new Grade();
        grade.setStudentId(student.getId());
        grade.setCourseId(course.getId());
        grade.setSemesterId(semester.getId());
        grade.setMidtermGrade(midtermScore);
        grade.setFinalGrade(finalScore);


        // Set result and close dialog
        this.result = grade;
        dispose();
    }

    private boolean validateInput(User student, Course course, Semester semester) {
        if (student == null) {
            showError("Please select a student");
            cboStudent.requestFocus();
            return false;
        }

        if (course == null) {
            showError("Please select a course");
            cboCourse.requestFocus();
            return false;
        }

        if (semester == null) {
            showError("Please select a semester");
            cboSemester.requestFocus();
            return false;
        }

        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static Grade showDialog(Frame parent, List<User> students, List<Course> courses, List<Semester> semesters) {
        AddGradeForm dialog = new AddGradeForm(parent, students, courses, semesters);
        dialog.setVisible(true);
        return dialog.getResult();
    }
}