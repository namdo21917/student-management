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
public class AddCourseForm extends JDialog {
    private JTextField txtCode;
    private JTextField txtName;
    private JSpinner spnCredits;
    private JComboBox<Major> cboMajor;
    private JComboBox<Teacher> cboTeacher;
    private JTextArea txtDescription;
    private JButton btnSave;
    private JButton btnCancel;

    @Getter
    private Course result = null;

    public AddCourseForm(Frame parent, List<Major> majors, List<Teacher> teachers) {
        super(parent, "Add New Course", true);
        initComponents(majors, teachers);
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
        String code = txtCode.getText().trim();
        String name = txtName.getText().trim();
        int credits = (Integer) spnCredits.getValue();
        Major major = (Major) cboMajor.getSelectedItem();
        Teacher teacher = (Teacher) cboTeacher.getSelectedItem();
        String description = txtDescription.getText().trim();

        // Validate input
        if (!validateInput(code, name, description)) {
            return;
        }

        Course course = new Course();
        course.setCode(code);
        course.setName(name);
        course.setCredit(credits);
        course.setMajorId(major.getId());
        course.setDeleted(false);

        // Set result and close dialog
        this.result = course;
        dispose();
    }

    private boolean validateInput(String code, String name, String description) {
        if (ValidationUtil.isNullOrEmpty(code)) {
            showError("Please enter course code");
            txtCode.requestFocus();
            return false;
        }

        if (!code.matches("[A-Z0-9]{3,10}")) {
            showError("Course code must be 3-10 characters long and contain only uppercase letters and numbers");
            txtCode.requestFocus();
            return false;
        }

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

    public static Course showDialog(Frame parent, List<Major> majors, List<Teacher> teachers) {
        AddCourseForm dialog = new AddCourseForm(parent, majors, teachers);
        dialog.setVisible(true);
        return dialog.getResult();
    }
}