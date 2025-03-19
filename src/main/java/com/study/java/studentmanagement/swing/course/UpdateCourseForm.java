package com.study.java.studentmanagement.swing.course;

import com.study.java.studentmanagement.model.Course;
import com.study.java.studentmanagement.model.Major;
import com.study.java.studentmanagement.model.Teacher;
import com.study.java.studentmanagement.util.ValidationUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

@Slf4j
public class UpdateCourseForm extends JDialog {
    private JTextField txtCode;
    private JTextField txtName;
    private JSpinner spnCredits;
    private JComboBox<Major> cboMajor;
    private JComboBox<Teacher> cboTeacher;
    private JTextArea txtDescription;
    private JButton btnUpdate;
    private JButton btnCancel;

    @Getter
    private Course result = null;
    private final Course course;

    public UpdateCourseForm(Frame parent, Course course, List<Major> majors, List<Teacher> teachers) {
        super(parent, "Update Course", true);
        this.course = course;
        initComponents(majors, teachers);
        loadCourseData();
        setupListeners();
    }

    private void initComponents(List<Major> majors, List<Teacher> teachers) {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(500, 600);
        setLocationRelativeTo(getOwner());

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Course Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Course Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Course Code:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtCode = new JTextField(20);
        formPanel.add(txtCode, gbc);

        // Course Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Course Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtName = new JTextField(20);
        formPanel.add(txtName, gbc);

        // Credits
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Credits:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerNumberModel creditsModel = new SpinnerNumberModel(3, 1, 10, 1);
        spnCredits = new JSpinner(creditsModel);
        formPanel.add(spnCredits, gbc);

        // Major
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Major:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cboMajor = new JComboBox<>(majors.toArray(new Major[0]));
        formPanel.add(cboMajor, gbc);

        // Teacher
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Teacher:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cboTeacher = new JComboBox<>(teachers.toArray(new Teacher[0]));
        formPanel.add(cboTeacher, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridheight = 2;
        txtDescription = new JTextArea(5, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        formPanel.add(scrollPane, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnUpdate = new JButton("Update");
        btnCancel = new JButton("Cancel");
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnCancel);

        // Add panels to main panel
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add main panel to dialog
        add(mainPanel);
    }

    private void loadCourseData() {
        txtCode.setText(course.getCode());
        txtName.setText(course.getName());
        spnCredits.setValue(course.getCredits());
        txtDescription.setText(course.getDescription());

        // Set selected major
        for (int i = 0; i < cboMajor.getItemCount(); i++) {
            Major major = cboMajor.getItemAt(i);
            if (major.getId().equals(course.getMajor().getId())) {
                cboMajor.setSelectedIndex(i);
                break;
            }
        }

        // Set selected teacher
        for (int i = 0; i < cboTeacher.getItemCount(); i++) {
            Teacher teacher = cboTeacher.getItemAt(i);
            if (teacher.getId().equals(course.getTeacher().getId())) {
                cboTeacher.setSelectedIndex(i);
                break;
            }
        }

        // Disable code field as it shouldn't be changed
        txtCode.setEnabled(false);
    }

    private void setupListeners() {
        btnUpdate.addActionListener(e -> handleUpdate());
        btnCancel.addActionListener(e -> dispose());
    }

    private void handleUpdate() {
        // Get values from form
        String name = txtName.getText().trim();
        int credits = (Integer) spnCredits.getValue();
        Major major = (Major) cboMajor.getSelectedItem();
        Teacher teacher = (Teacher) cboTeacher.getSelectedItem();
        String description = txtDescription.getText().trim();

        // Validate input
        if (!validateInput(name, description)) {
            return;
        }

        // Create updated course
        Course updatedCourse = Course.builder()
                .id(course.getId())
                .code(course.getCode())
                .name(name)
                .credits(credits)
                .major(major)
                .teacher(teacher)
                .description(description)
                .build();

        // Set result and close dialog
        this.result = updatedCourse;
        dispose();
    }

    private boolean validateInput(String name, String description) {
        if (ValidationUtil.isNullOrEmpty(name)) {
            showError("Please enter course name");
            txtName.requestFocus();
            return false;
        }

        if (name.length() < 3 || name.length() > 100) {
            showError("Course name must be between 3 and 100 characters");
            txtName.requestFocus();
            return false;
        }

        if (ValidationUtil.isNullOrEmpty(description)) {
            showError("Please enter course description");
            txtDescription.requestFocus();
            return false;
        }

        if (description.length() < 10 || description.length() > 500) {
            showError("Course description must be between 10 and 500 characters");
            txtDescription.requestFocus();
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

    public static Course showDialog(Frame parent, Course course, List<Major> majors, List<Teacher> teachers) {
        UpdateCourseForm dialog = new UpdateCourseForm(parent, course, majors, teachers);
        dialog.setVisible(true);
        return dialog.getResult();
    }
}