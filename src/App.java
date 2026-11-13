import java.io.File;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application {

    private Stage stage;
    private StudentManager manager;

    private ArrayList<MarksRecord> marksRecords;
    private ArrayList<AttendanceRecord> attendanceRecords;

    private static final String STUDENT_PASSWORD = "student123";
    private static final String TEACHER_ID = "T001";
    private static final String TEACHER_PASSWORD = "teach123";

    @Override
    public void start(Stage primaryStage) {

        stage = primaryStage;

        manager = new StudentManager();
        manager.loadRecords();

        reloadMarks();
        reloadAttendance();

        showLoginScreen();

        stage.setTitle("Smart Student Academic Performance Analyzer");
        stage.show();
    }

    // ---------------- DATA RELOAD ----------------

    private void reloadMarks() {
        marksRecords = MarksFileHandler.loadMarks();

        if (marksRecords == null) {
            marksRecords = new ArrayList<>();
        }
    }

    private void reloadAttendance() {
        attendanceRecords = AttendanceFileHandler.loadAttendance();

        if (attendanceRecords == null) {
            attendanceRecords = new ArrayList<>();
        }
    }

    // ---------------- COMMON HELPERS ----------------

    private VBox createCard(String heading, String value) {

        Label headingLabel = new Label(heading);
        headingLabel.setStyle(
                "-fx-font-size: 13px;"
                + "-fx-text-fill: #D8EEFF;"
                + "-fx-font-weight: bold;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setStyle(
                "-fx-font-size: 23px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: white;"
        );

        VBox card = new VBox(10, headingLabel, valueLabel);

        card.setPadding(new Insets(18));
        card.setAlignment(Pos.CENTER_LEFT);

        card.setStyle(
                "-fx-background-color: rgba(5,75,145,0.90);"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: #72C5FF;"
                + "-fx-border-radius: 12;"
        );

        card.setMinWidth(180);
        card.setMaxWidth(Double.MAX_VALUE);

        return card;
    }

    private double calculateOverallAttendance(String studentId) {

        int total = 0;
        int attended = 0;

        for (AttendanceRecord record : attendanceRecords) {

            if (record.getStudentId().equalsIgnoreCase(studentId)) {
                total += record.getTotalClasses();
                attended += record.getAttendedClasses();
            }
        }

        if (total == 0) {
            return 0;
        }

        return attended * 100.0 / total;
    }

    private String getAttendanceStatus(double percentage) {

        return percentage >= 75 ? "Eligible" : "Shortage";
    }

    private ArrayList<MarksRecord> getStudentMarks(String studentId) {

        ArrayList<MarksRecord> result = new ArrayList<>();

        for (MarksRecord record : marksRecords) {

            if (record.getStudentId().equalsIgnoreCase(studentId)) {
                result.add(record);
            }
        }

        return result;
    }

    private ArrayList<AttendanceRecord> getStudentAttendance(
            String studentId) {

        ArrayList<AttendanceRecord> result = new ArrayList<>();

        for (AttendanceRecord record : attendanceRecords) {

            if (record.getStudentId().equalsIgnoreCase(studentId)) {
                result.add(record);
            }
        }

        return result;
    }

    // ---------------- LOGIN SCREEN ----------------

    private void showLoginScreen() {

        Label title = new Label("SMART STUDENT");
        title.setStyle(
                "-fx-font-size: 25px; -fx-font-weight: bold;"
        );

        Label subtitle =
                new Label("Academic Performance Analyzer");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Student", "Teacher");
        roleBox.setValue("Student");

        TextField idField = new TextField();
        idField.setPromptText("Student ID / Teacher ID");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("LOGIN");
        loginButton.setPrefWidth(180);

        Label message = new Label();

        loginButton.setOnAction(e -> {

            String role = roleBox.getValue();
            String id = idField.getText().trim();
            String password = passwordField.getText();

            if (id.isEmpty() || password.isEmpty()) {
                message.setText("Please enter all details.");
                return;
            }

            if ("Teacher".equals(role)) {

                if (id.equals(TEACHER_ID)
                        && password.equals(TEACHER_PASSWORD)) {

                    showTeacherDashboard();

                } else {
                    message.setText("Invalid teacher ID or password.");
                }

            } else {

                Student student = manager.searchStudent(id);

                if (student != null
                        && password.equals(STUDENT_PASSWORD)) {

                    showStudentDashboard(student);

                } else {
                    message.setText("Invalid student ID or password.");
                }
            }
        });

        VBox layout = new VBox(
                15,
                title,
                subtitle,
                roleBox,
                idField,
                passwordField,
                loginButton,
                message
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));

        layout.getStyleClass().add("modern-background");

        stage.setScene(createStyledScene(layout, 550, 500));
    }

    // ---------------- TEACHER DASHBOARD ----------------

    private void showTeacherDashboard() {

        Label heading = new Label("TEACHER DASHBOARD");
        heading.setStyle(
                "-fx-font-size: 25px; -fx-font-weight: bold;"
        );

        Label welcome = new Label("Welcome, Teacher!");

        HBox summaryCards = createTeacherSummaryCards();

        TabPane tabs = new TabPane();

        Tab addStudentTab = new Tab("Add Student");
        addStudentTab.setContent(createAddStudentTab());

        Tab viewStudentsTab = new Tab("View Students");
        viewStudentsTab.setContent(createViewStudentsTab());

        Tab marksTab = new Tab("Enter Marks");
        marksTab.setContent(createEnterMarksTab());

        Tab attendanceTab = new Tab("Attendance");
        attendanceTab.setContent(createAttendanceTab());

        Tab viewMarksTab = new Tab("View Marks");
        viewMarksTab.setContent(createViewMarksTab());

        Tab viewAttendanceTab = new Tab("View Attendance");
        viewAttendanceTab.setContent(createViewAttendanceTab());

        Tab performanceTab = new Tab("Performance Analysis");
        performanceTab.setContent(createTeacherPerformanceTab());

        Tab attendanceAnalysisTab = new Tab("Attendance Analysis");
        attendanceAnalysisTab.setContent(
                createTeacherAttendanceAnalysisTab()
        );

        tabs.getTabs().addAll(
                addStudentTab,
                viewStudentsTab,
                marksTab,
                attendanceTab,
                viewMarksTab,
                viewAttendanceTab,
                performanceTab,
                attendanceAnalysisTab
        );

        tabs.setTabClosingPolicy(
                TabPane.TabClosingPolicy.UNAVAILABLE
        );

        Button refreshDashboard = new Button("Refresh Dashboard");

        refreshDashboard.setOnAction(e -> {
            reloadMarks();
            reloadAttendance();
            showTeacherDashboard();
        });

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> showLoginScreen());

        VBox layout = new VBox(
                15,
                heading,
                welcome,
                summaryCards,
                refreshDashboard,
                tabs,
                logoutButton
        );

        layout.setPadding(new Insets(20));

        layout.getStyleClass().add("modern-background");

        stage.setScene(createStyledScene(layout, 1150, 850));
    }

    // ---------------- TEACHER SUMMARY CARDS ----------------

    private HBox createTeacherSummaryCards() {

        int students = manager.getStudents().size();
        int marks = marksRecords.size();
        int attendance = attendanceRecords.size();

        int totalClasses = 0;
        int attendedClasses = 0;

        for (AttendanceRecord record : attendanceRecords) {
            totalClasses += record.getTotalClasses();
            attendedClasses += record.getAttendedClasses();
        }

        double percentage = 0;

        if (totalClasses > 0) {
            percentage = attendedClasses * 100.0 / totalClasses;
        }

        VBox studentCard = createCard(
                "Total Students",
                String.valueOf(students)
        );

        VBox marksCard = createCard(
                "Marks Records",
                String.valueOf(marks)
        );

        VBox attendanceCard = createCard(
                "Attendance Records",
                String.valueOf(attendance)
        );

        VBox percentageCard = createCard(
                "Overall Attendance",
                String.format("%.2f%%", percentage)
        );

        HBox cards = new HBox(
                15,
                studentCard,
                marksCard,
                attendanceCard,
                percentageCard
        );

        cards.setAlignment(Pos.CENTER);
        cards.setMaxWidth(Double.MAX_VALUE);

        return cards;
    }

    // ---------------- ADD STUDENT ----------------

    private VBox createAddStudentTab() {

        Label title = new Label("Register New Student");

        TextField idField = new TextField();
        idField.setPromptText("Student ID");

        TextField nameField = new TextField();
        nameField.setPromptText("Student Name");

        TextField departmentField = new TextField();
        departmentField.setPromptText("Department");

        TextField sectionField = new TextField();
        sectionField.setPromptText("Section");

        Button addButton = new Button("Add Student");

        Label message = new Label();

        addButton.setOnAction(e -> {

            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String department = departmentField.getText().trim();
            String section = sectionField.getText().trim();

            if (id.isEmpty() || name.isEmpty()
                    || department.isEmpty() || section.isEmpty()) {

                message.setText("Please fill all fields.");
                return;
            }

            if (manager.searchStudent(id) != null) {
                message.setText("Student ID already exists.");
                return;
            }

            Student student = new Student(
                    id, name, department, section
            );

            manager.addStudent(student);
            manager.saveRecords();

            message.setText("Student added successfully!");

            idField.clear();
            nameField.clear();
            departmentField.clear();
            sectionField.clear();
        });

        VBox layout = new VBox(
                15,
                title,
                idField,
                nameField,
                departmentField,
                sectionField,
                addButton,
                message
        );

        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    // ---------------- VIEW AND EDIT STUDENTS ----------------

    private VBox createViewStudentsTab() {

        Label title = new Label("Student Records");

        TableView<Student> table = new TableView<>();

        TableColumn<Student, String> idColumn =
                new TableColumn<>("Student ID");

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("studentId")
        );

        TableColumn<Student, String> nameColumn =
                new TableColumn<>("Name");

        nameColumn.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        TableColumn<Student, String> departmentColumn =
                new TableColumn<>("Department");

        departmentColumn.setCellValueFactory(
                new PropertyValueFactory<>("department")
        );

        TableColumn<Student, String> sectionColumn =
                new TableColumn<>("Section");

        sectionColumn.setCellValueFactory(
                new PropertyValueFactory<>("section")
        );
        table.getColumns().add(idColumn);
        table.getColumns().add(nameColumn);
        table.getColumns().add(departmentColumn);
        table.getColumns().add(sectionColumn);

        
       table.setColumnResizePolicy(
        TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
);

        ObservableList<Student> studentList =
                FXCollections.observableArrayList(
                        manager.getStudents()
                );

        table.setItems(studentList);

        TextField searchField = new TextField();
        searchField.setPromptText(
                "Search by ID, name, department or section"
        );

        Button searchButton = new Button("Search");
        Button refreshButton = new Button("Refresh");
        Button clearSearchButton = new Button("Show All");

        Button editButton = new Button("Edit Selected Student");
        Button deleteButton = new Button("Delete Student");

        TextField nameField = new TextField();
        nameField.setPromptText("Updated Name");

        TextField departmentField = new TextField();
        departmentField.setPromptText("Updated Department");

        TextField sectionField = new TextField();
        sectionField.setPromptText("Updated Section");

        Label message = new Label();

        searchButton.setOnAction(e -> {

            String query = searchField.getText()
                    .trim().toLowerCase();

            if (query.isEmpty()) {
                message.setText("Enter a search value.");
                return;
            }

            ObservableList<Student> filtered =
                    FXCollections.observableArrayList();

            for (Student student : manager.getStudents()) {

                if (student.getStudentId().toLowerCase().contains(query)
                        || student.getName().toLowerCase().contains(query)
                        || student.getDepartment().toLowerCase().contains(query)
                        || student.getSection().toLowerCase().contains(query)) {

                    filtered.add(student);
                }
            }

            studentList.setAll(filtered);

            message.setText(
                    filtered.size() + " student(s) found."
            );
        });

        clearSearchButton.setOnAction(e -> {
            searchField.clear();
            studentList.setAll(manager.getStudents());
            message.setText("Showing all students.");
        });

        refreshButton.setOnAction(e -> {
            studentList.setAll(manager.getStudents());
            message.setText("Records refreshed.");
        });

        editButton.setOnAction(e -> {

            Student selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                message.setText("Select a student first.");
                return;
            }

            nameField.setText(selected.getName());
            departmentField.setText(selected.getDepartment());
            sectionField.setText(selected.getSection());

            message.setText(
                    "Update the details below and click Save Changes."
            );
        });

        Button saveEditButton = new Button("Save Changes");

        saveEditButton.setOnAction(e -> {

            Student selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                message.setText("Select a student first.");
                return;
            }

            String name = nameField.getText().trim();
            String department = departmentField.getText().trim();
            String section = sectionField.getText().trim();

            if (name.isEmpty() || department.isEmpty()
                    || section.isEmpty()) {

                message.setText("Fill all update fields.");
                return;
            }

            String id = selected.getStudentId();

            manager.deleteStudent(id);

            manager.addStudent(
                    new Student(id, name, department, section)
            );

            manager.saveRecords();

            studentList.setAll(manager.getStudents());

            nameField.clear();
            departmentField.clear();
            sectionField.clear();

            message.setText("Student details updated successfully.");
        });

        deleteButton.setOnAction(e -> {

            Student selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                message.setText("Select a student first.");
                return;
            }

            Alert confirmation = new Alert(
                    Alert.AlertType.CONFIRMATION
            );

            confirmation.setTitle("Delete Student");
            confirmation.setHeaderText("Delete selected student?");
            confirmation.setContentText(
                    "This will also remove their marks and attendance records."
            );

            if (confirmation.showAndWait().orElse(
                    ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }

            String id = selected.getStudentId();

            manager.deleteStudent(id);
            manager.saveRecords();

            marksRecords.removeIf(
                    record -> record.getStudentId().equalsIgnoreCase(id)
            );

            attendanceRecords.removeIf(
                    record -> record.getStudentId().equalsIgnoreCase(id)
            );

            MarksFileHandler.saveMarks(marksRecords);
            AttendanceFileHandler.saveAttendance(attendanceRecords);

            studentList.setAll(manager.getStudents());

            message.setText("Student and related records deleted.");
        });

        HBox searchLayout = new HBox(
                10,
                searchField,
                searchButton,
                clearSearchButton,
                refreshButton
        );

        HBox.setHgrow(searchField, Priority.ALWAYS);

        HBox editLayout = new HBox(
                10,
                nameField,
                departmentField,
                sectionField,
                saveEditButton
        );

        VBox layout = new VBox(
                15,
                title,
                searchLayout,
                table,
                editButton,
                new Label("Edit Student Details"),
                editLayout,
                deleteButton,
                message
        );

        layout.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);

        return layout;
    }

    // ---------------- ENTER MARKS ----------------

    private VBox createEnterMarksTab() {

        Label title = new Label("Enter Student Marks");

        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Student ID");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject Name");

        TextField internalField = new TextField();
        internalField.setPromptText("Internal Marks (Maximum 30)");

        TextField examField = new TextField();
        examField.setPromptText("Semester Marks (Maximum 70)");

        Button saveButton = new Button("Save Marks");

        Label message = new Label();

        saveButton.setOnAction(e -> {

            try {

                String studentId = studentIdField.getText().trim();
                String subject = subjectField.getText().trim();

                if (studentId.isEmpty() || subject.isEmpty()
                        || internalField.getText().trim().isEmpty()
                        || examField.getText().trim().isEmpty()) {

                    message.setText("Please fill all fields.");
                    return;
                }

                if (manager.searchStudent(studentId) == null) {
                    message.setText("Student ID does not exist.");
                    return;
                }

                int internal = Integer.parseInt(
                        internalField.getText().trim()
                );

                int exam = Integer.parseInt(
                        examField.getText().trim()
                );

                if (internal < 0 || internal > 30
                        || exam < 0 || exam > 70) {

                    message.setText(
                            "Internal must be 0-30 and exam must be 0-70."
                    );
                    return;
                }

                for (MarksRecord record : marksRecords) {

                    if (record.getStudentId().equalsIgnoreCase(studentId)
                            && record.getSubjectName().equalsIgnoreCase(subject)) {

                        message.setText(
                                "Marks already entered for this subject."
                        );
                        return;
                    }
                }

                marksRecords.add(
                        new MarksRecord(
                                studentId, subject, internal, exam
                        )
                );

                MarksFileHandler.saveMarks(marksRecords);

                message.setText("Marks saved successfully!");

                studentIdField.clear();
                subjectField.clear();
                internalField.clear();
                examField.clear();

            } catch (NumberFormatException ex) {
                message.setText("Enter valid numeric marks.");
            } catch (Exception ex) {
                message.setText("Unable to save marks.");
            }
        });

        VBox layout = new VBox(
                15,
                title,
                studentIdField,
                subjectField,
                internalField,
                examField,
                saveButton,
                message
        );

        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    // ---------------- ENTER ATTENDANCE ----------------

    private VBox createAttendanceTab() {

        Label title = new Label("Enter Student Attendance");

        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Student ID");

        TextField subjectField = new TextField();
        subjectField.setPromptText("Subject Name");

        TextField totalClassesField = new TextField();
        totalClassesField.setPromptText("Total Classes");

        TextField attendedClassesField = new TextField();
        attendedClassesField.setPromptText("Attended Classes");

        Button saveButton = new Button("Save Attendance");

        Label message = new Label();

        saveButton.setOnAction(e -> {

            try {

                String studentId = studentIdField.getText().trim();
                String subject = subjectField.getText().trim();

                if (studentId.isEmpty() || subject.isEmpty()
                        || totalClassesField.getText().trim().isEmpty()
                        || attendedClassesField.getText().trim().isEmpty()) {

                    message.setText("Please fill all fields.");
                    return;
                }

                if (manager.searchStudent(studentId) == null) {
                    message.setText("Student ID does not exist.");
                    return;
                }

                int totalClasses = Integer.parseInt(
                        totalClassesField.getText().trim()
                );

                int attendedClasses = Integer.parseInt(
                        attendedClassesField.getText().trim()
                );

                if (totalClasses <= 0
                        || attendedClasses < 0
                        || attendedClasses > totalClasses) {

                    message.setText("Enter valid attendance values.");
                    return;
                }

                for (AttendanceRecord record : attendanceRecords) {

                    if (record.getStudentId().equalsIgnoreCase(studentId)
                            && record.getSubjectName().equalsIgnoreCase(subject)) {

                        message.setText(
                                "Attendance already entered for this subject."
                        );
                        return;
                    }
                }

                attendanceRecords.add(
                        new AttendanceRecord(
                                studentId,
                                subject,
                                totalClasses,
                                attendedClasses
                        )
                );

                AttendanceFileHandler.saveAttendance(
                        attendanceRecords
                );

                message.setText("Attendance saved successfully!");

                studentIdField.clear();
                subjectField.clear();
                totalClassesField.clear();
                attendedClassesField.clear();

            } catch (NumberFormatException ex) {
                message.setText("Enter valid numeric values.");
            } catch (Exception ex) {
                message.setText("Unable to save attendance.");
            }
        });

        VBox layout = new VBox(
                15,
                title,
                studentIdField,
                subjectField,
                totalClassesField,
                attendedClassesField,
                saveButton,
                message
        );

        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);

        return layout;
    }

    // ---------------- VIEW AND EDIT MARKS ----------------

    private VBox createViewMarksTab() {

        Label title = new Label("Student Marks Records");

        TableView<MarksRecord> table = createMarksTable();

        table.setItems(
                FXCollections.observableArrayList(marksRecords)
        );

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Student ID or Subject");

        Button searchButton = new Button("Search");
        Button showAllButton = new Button("Show All");
        Button refreshButton = new Button("Refresh");

        TextField internalField = new TextField();
        internalField.setPromptText("New Internal Marks / 30");

        TextField examField = new TextField();
        examField.setPromptText("New Exam Marks / 70");

        Button editButton = new Button("Update Selected Marks");

        Label message = new Label();

        searchButton.setOnAction(e -> {

            String query = searchField.getText()
                    .trim().toLowerCase();

            if (query.isEmpty()) {
                message.setText("Enter a search value.");
                return;
            }

            ArrayList<MarksRecord> filtered = new ArrayList<>();

            for (MarksRecord record : marksRecords) {

                if (record.getStudentId().toLowerCase().contains(query)
                        || record.getSubjectName().toLowerCase().contains(query)) {

                    filtered.add(record);
                }
            }

            table.setItems(
                    FXCollections.observableArrayList(filtered)
            );

            message.setText(filtered.size() + " record(s) found.");
        });

        showAllButton.setOnAction(e -> {
            searchField.clear();

            table.setItems(
                    FXCollections.observableArrayList(marksRecords)
            );

            message.setText("Showing all marks.");
        });

        refreshButton.setOnAction(e -> {
            reloadMarks();

            table.setItems(
                    FXCollections.observableArrayList(marksRecords)
            );

            message.setText("Marks refreshed.");
        });

        editButton.setOnAction(e -> {

            MarksRecord selected =
                    table.getSelectionModel().getSelectedItem();

            if (selected == null) {
                message.setText("Select a marks record first.");
                return;
            }

            try {

                if (internalField.getText().trim().isEmpty()
                        || examField.getText().trim().isEmpty()) {

                    message.setText("Enter both marks.");
                    return;
                }

                int internal = Integer.parseInt(
                        internalField.getText().trim()
                );

                int exam = Integer.parseInt(
                        examField.getText().trim()
                );

                if (internal < 0 || internal > 30
                        || exam < 0 || exam > 70) {

                    message.setText(
                            "Internal must be 0-30 and exam must be 0-70."
                    );
                    return;
                }

                int index = marksRecords.indexOf(selected);

                if (index < 0) {
                    message.setText("Record not found. Refresh the table.");
                    return;
                }

                marksRecords.set(
                        index,
                        new MarksRecord(
                                selected.getStudentId(),
                                selected.getSubjectName(),
                                internal,
                                exam
                        )
                );

                MarksFileHandler.saveMarks(marksRecords);

                table.setItems(
                        FXCollections.observableArrayList(marksRecords)
                );

                internalField.clear();
                examField.clear();

                message.setText("Marks updated successfully.");

            } catch (NumberFormatException ex) {
                message.setText("Enter valid numeric marks.");
            }
        });

        HBox searchLayout = new HBox(
                10,
                searchField,
                searchButton,
                showAllButton,
                refreshButton
        );

        HBox editLayout = new HBox(
                10,
                internalField,
                examField,
                editButton
        );

        HBox.setHgrow(searchField, Priority.ALWAYS);

        VBox layout = new VBox(
                15,
                title,
                searchLayout,
                table,
                new Label("Edit Selected Marks"),
                editLayout,
                message
        );

        layout.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);

        return layout;
    }

    // ---------------- MARKS TABLE ----------------

    private TableView<MarksRecord> createMarksTable() {

        TableView<MarksRecord> table = new TableView<>();

        TableColumn<MarksRecord, String> idColumn =
                new TableColumn<>("Student ID");

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("studentId")
        );

        TableColumn<MarksRecord, String> subjectColumn =
                new TableColumn<>("Subject");

        subjectColumn.setCellValueFactory(
                new PropertyValueFactory<>("subjectName")
        );

        TableColumn<MarksRecord, Integer> internalColumn =
                new TableColumn<>("Internal / 30");

        internalColumn.setCellValueFactory(
                new PropertyValueFactory<>("internalMarks")
        );

        TableColumn<MarksRecord, Integer> examColumn =
                new TableColumn<>("Exam / 70");

        examColumn.setCellValueFactory(
                new PropertyValueFactory<>("examMarks")
        );

        TableColumn<MarksRecord, Integer> totalColumn =
                new TableColumn<>("Total / 100");

        totalColumn.setCellValueFactory(
                new PropertyValueFactory<>("totalMarks")
        );

        TableColumn<MarksRecord, Double> percentageColumn =
                new TableColumn<>("Percentage");

        percentageColumn.setCellValueFactory(
                new PropertyValueFactory<>("percentage")
        );

        TableColumn<MarksRecord, String> gradeColumn =
                new TableColumn<>("Grade");

        gradeColumn.setCellValueFactory(
                new PropertyValueFactory<>("grade")
        );

        TableColumn<MarksRecord, String> statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

       table.getColumns().add(idColumn);
table.getColumns().add(subjectColumn);
table.getColumns().add(internalColumn);
table.getColumns().add(examColumn);
table.getColumns().add(totalColumn);
table.getColumns().add(percentageColumn);
table.getColumns().add(gradeColumn);
table.getColumns().add(statusColumn);

table.setColumnResizePolicy(
        TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
);

        return table;
    }

    // ---------------- VIEW ATTENDANCE ----------------

    private VBox createViewAttendanceTab() {

        Label title = new Label("Student Attendance Records");

        TableView<AttendanceRecord> table =
                createAttendanceTable();

        table.setItems(
                FXCollections.observableArrayList(attendanceRecords)
        );

        TextField searchField = new TextField();
        searchField.setPromptText("Search by Student ID or Subject");

        Button searchButton = new Button("Search");
        Button showAllButton = new Button("Show All");
        Button refreshButton = new Button("Refresh");

        Label message = new Label();

        searchButton.setOnAction(e -> {

            String query = searchField.getText()
                    .trim().toLowerCase();

            if (query.isEmpty()) {
                message.setText("Enter a search value.");
                return;
            }

            ArrayList<AttendanceRecord> filtered = new ArrayList<>();

            for (AttendanceRecord record : attendanceRecords) {

                if (record.getStudentId().toLowerCase().contains(query)
                        || record.getSubjectName().toLowerCase().contains(query)) {

                    filtered.add(record);
                }
            }

            table.setItems(
                    FXCollections.observableArrayList(filtered)
            );

            message.setText(filtered.size() + " record(s) found.");
        });

        showAllButton.setOnAction(e -> {
            searchField.clear();

            table.setItems(
                    FXCollections.observableArrayList(attendanceRecords)
            );

            message.setText("Showing all attendance records.");
        });

        refreshButton.setOnAction(e -> {
            reloadAttendance();

            table.setItems(
                    FXCollections.observableArrayList(attendanceRecords)
            );

            message.setText("Attendance refreshed.");
        });

        HBox searchLayout = new HBox(
                10,
                searchField,
                searchButton,
                showAllButton,
                refreshButton
        );

        HBox.setHgrow(searchField, Priority.ALWAYS);

        VBox layout = new VBox(
                15,
                title,
                searchLayout,
                table,
                message
        );

        layout.setPadding(new Insets(20));
        VBox.setVgrow(table, Priority.ALWAYS);

        return layout;
    }

    // ---------------- ATTENDANCE TABLE ----------------

    private TableView<AttendanceRecord> createAttendanceTable() {

        TableView<AttendanceRecord> table = new TableView<>();

        TableColumn<AttendanceRecord, String> idColumn =
                new TableColumn<>("Student ID");

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("studentId")
        );

        TableColumn<AttendanceRecord, String> subjectColumn =
                new TableColumn<>("Subject");

        subjectColumn.setCellValueFactory(
                new PropertyValueFactory<>("subjectName")
        );

        TableColumn<AttendanceRecord, Integer> totalColumn =
                new TableColumn<>("Total Classes");

        totalColumn.setCellValueFactory(
                new PropertyValueFactory<>("totalClasses")
        );

        TableColumn<AttendanceRecord, Integer> attendedColumn =
                new TableColumn<>("Attended Classes");

        attendedColumn.setCellValueFactory(
                new PropertyValueFactory<>("attendedClasses")
        );

        TableColumn<AttendanceRecord, Double> percentageColumn =
                new TableColumn<>("Attendance %");

        percentageColumn.setCellValueFactory(
                new PropertyValueFactory<>("attendancePercentage")
        );

        TableColumn<AttendanceRecord, String> statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("attendanceStatus")
        );

       table.getColumns().add(idColumn);
        table.getColumns().add(subjectColumn);
        table.getColumns().add(totalColumn);
        table.getColumns().add(attendedColumn);
        table.getColumns().add(percentageColumn);
        table.getColumns().add(statusColumn);

        table.setColumnResizePolicy(
        TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
);
        return table;
    }

    // ---------------- TEACHER PERFORMANCE ANALYSIS ----------------

    private VBox createTeacherPerformanceTab() {

        Label title = new Label(
                "Student-wise Performance Analysis"
        );

        title.setStyle(
                "-fx-font-size: 20px; -fx-font-weight: bold;"
        );

        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Enter Student ID");

        Button searchButton = new Button("Analyze Performance");

        Label studentNameLabel = new Label();
        Label averageLabel = new Label();
        Label highestLabel = new Label();
        Label lowestLabel = new Label();
        Label gradeLabel = new Label();
        Label statusLabel = new Label();

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(350);

        TableView<MarksRecord> marksTable = createMarksTable();

        Label message = new Label();

        searchButton.setOnAction(e -> {

            String studentId = studentIdField.getText().trim();

            if (studentId.isEmpty()) {
                message.setText("Please enter a Student ID.");
                marksTable.getItems().clear();
                return;
            }

            Student student = manager.searchStudent(studentId);

            if (student == null) {
                message.setText("Student not found.");
                marksTable.getItems().clear();
                studentNameLabel.setText("");
                averageLabel.setText("");
                highestLabel.setText("");
                lowestLabel.setText("");
                gradeLabel.setText("");
                statusLabel.setText("");
                progressBar.setProgress(0);
                return;
            }

            ArrayList<MarksRecord> studentMarks =
                    getStudentMarks(studentId);

            marksTable.setItems(
                    FXCollections.observableArrayList(studentMarks)
            );

            studentNameLabel.setText(
                    "Student Name: " + student.getName()
                    + "     Student ID: " + student.getStudentId()
            );

            if (studentMarks.isEmpty()) {

                message.setText("No marks available for this student.");

                averageLabel.setText("Average Marks: Not available");
                highestLabel.setText("Highest Marks: Not available");
                lowestLabel.setText("Lowest Marks: Not available");
                gradeLabel.setText("Overall Grade: Not available");
                statusLabel.setText("Performance Status: Not available");

                progressBar.setProgress(0);
                return;
            }

            int total = 0;
            int highest = 0;
            int lowest = 100;

            for (MarksRecord record : studentMarks) {

                int marks = record.getTotalMarks();

                total += marks;
                highest = Math.max(highest, marks);
                lowest = Math.min(lowest, marks);
            }

            double average =
                    (double) total / studentMarks.size();

            String grade = calculateGrade(average);
            String status = calculatePerformanceStatus(average);

            averageLabel.setText(
                    String.format("Average Marks: %.2f / 100", average)
            );

            highestLabel.setText("Highest Marks: " + highest);
            lowestLabel.setText("Lowest Marks: " + lowest);
            gradeLabel.setText("Overall Grade: " + grade);
            statusLabel.setText("Performance Status: " + status);

            progressBar.setProgress(average / 100.0);

            message.setText(
                    "Analysis completed for " + student.getName()
            );
        });

        VBox summary = new VBox(
                10,
                studentNameLabel,
                averageLabel,
                highestLabel,
                lowestLabel,
                gradeLabel,
                statusLabel,
                new Label("Average Performance"),
                progressBar
        );

        VBox layout = new VBox(
                15,
                title,
                studentIdField,
                searchButton,
                message,
                summary,
                new Label("Subject-wise Marks"),
                marksTable
        );

        layout.setPadding(new Insets(20));
        VBox.setVgrow(marksTable, Priority.ALWAYS);

        return layout;
    }

    // ---------------- ATTENDANCE ANALYSIS ----------------

    private VBox createTeacherAttendanceAnalysisTab() {

        Label title = new Label("Student-wise Attendance Analysis");

        title.setStyle(
                "-fx-font-size: 20px; -fx-font-weight: bold;"
        );

        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Enter Student ID");

        Button analyzeButton = new Button("Analyze Attendance");

        Label studentNameLabel = new Label();
        Label totalClassesLabel = new Label();
        Label attendedClassesLabel = new Label();
        Label percentageLabel = new Label();
        Label statusLabel = new Label();

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(350);

        TableView<AttendanceRecord> attendanceTable =
                createAttendanceTable();

        Label message = new Label();

        analyzeButton.setOnAction(e -> {

            String studentId = studentIdField.getText().trim();

            if (studentId.isEmpty()) {
                message.setText("Please enter a Student ID.");
                attendanceTable.getItems().clear();
                return;
            }

            Student student = manager.searchStudent(studentId);

            if (student == null) {
                message.setText("Student not found.");
                attendanceTable.getItems().clear();
                studentNameLabel.setText("");
                totalClassesLabel.setText("");
                attendedClassesLabel.setText("");
                percentageLabel.setText("");
                statusLabel.setText("");
                progressBar.setProgress(0);
                return;
            }

            ArrayList<AttendanceRecord> studentAttendance =
                    getStudentAttendance(studentId);

            attendanceTable.setItems(
                    FXCollections.observableArrayList(studentAttendance)
            );

            studentNameLabel.setText(
                    "Student Name: " + student.getName()
                    + "     Student ID: " + student.getStudentId()
            );

            if (studentAttendance.isEmpty()) {

                message.setText(
                        "No attendance records available for this student."
                );

                totalClassesLabel.setText("Total Classes: 0");
                attendedClassesLabel.setText("Attended Classes: 0");
                percentageLabel.setText("Attendance: Not available");
                statusLabel.setText("Status: Not available");

                progressBar.setProgress(0);
                return;
            }

            int totalClasses = 0;
            int attendedClasses = 0;

            for (AttendanceRecord record : studentAttendance) {

                totalClasses += record.getTotalClasses();
                attendedClasses += record.getAttendedClasses();
            }

            double percentage =
                    attendedClasses * 100.0 / totalClasses;

            String status = getAttendanceStatus(percentage);

            totalClassesLabel.setText(
                    "Total Classes: " + totalClasses
            );

            attendedClassesLabel.setText(
                    "Attended Classes: " + attendedClasses
            );

            percentageLabel.setText(
                    String.format("Overall Attendance: %.2f%%", percentage)
            );

            statusLabel.setText(
                    "Attendance Status: " + status
            );

            progressBar.setProgress(percentage / 100.0);

            message.setText(
                    "Attendance analysis completed for "
                    + student.getName()
            );
        });

        VBox summary = new VBox(
                10,
                studentNameLabel,
                totalClassesLabel,
                attendedClassesLabel,
                percentageLabel,
                statusLabel,
                new Label("Attendance Progress"),
                progressBar
        );

        Label rule = new Label(
                "Minimum attendance required: 75%"
        );

        VBox layout = new VBox(
                15,
                title,
                studentIdField,
                analyzeButton,
                message,
                summary,
                rule,
                new Label("Subject-wise Attendance"),
                attendanceTable
        );

        layout.setPadding(new Insets(20));
        VBox.setVgrow(attendanceTable, Priority.ALWAYS);

        return layout;
    }

    // ---------------- STUDENT DASHBOARD ----------------

    private void showStudentDashboard(Student student) {

        reloadMarks();
        reloadAttendance();

        Label heading = new Label("STUDENT DASHBOARD");

        heading.setStyle(
                "-fx-font-size: 25px; -fx-font-weight: bold;"
        );

        Label welcome = new Label(
                "Welcome, " + student.getName()
        );

        Label details = new Label(
                "Student ID: " + student.getStudentId()
                + "     Department: " + student.getDepartment()
                + "     Section: " + student.getSection()
        );

        HBox summaryCards = createStudentSummaryCards(student);

        TabPane studentTabs = new TabPane();

        Tab marksTab = new Tab("My Marks");

        TableView<MarksRecord> marksTable = createMarksTable();

        marksTable.setItems(
                FXCollections.observableArrayList(
                        getStudentMarks(student.getStudentId())
                )
        );

        marksTab.setContent(new VBox(10, marksTable));
        marksTab.setClosable(false);

        Tab attendanceTab = new Tab("My Attendance");

        TableView<AttendanceRecord> attendanceTable =
                createAttendanceTable();

        attendanceTable.setItems(
                FXCollections.observableArrayList(
                        getStudentAttendance(student.getStudentId())
                )
        );

        attendanceTab.setContent(new VBox(10, attendanceTable));
        attendanceTab.setClosable(false);

        Tab analysisTab = new Tab("Performance Analysis");

        analysisTab.setContent(
                createPerformanceAnalysis(student)
        );

        analysisTab.setClosable(false);

        Tab refreshTab = new Tab("Refresh");

        Button refreshButton = new Button("Refresh My Records");

        refreshButton.setOnAction(e -> {

            reloadMarks();
            reloadAttendance();

            marksTable.setItems(
                    FXCollections.observableArrayList(
                            getStudentMarks(student.getStudentId())
                    )
            );

            attendanceTable.setItems(
                    FXCollections.observableArrayList(
                            getStudentAttendance(student.getStudentId())
                    )
            );

            analysisTab.setContent(
                    createPerformanceAnalysis(student)
            );

            summaryCards.getChildren().setAll(
                    createStudentSummaryCards(student).getChildren()
            );
        });

        refreshTab.setContent(new VBox(15, refreshButton));
        refreshTab.setClosable(false);

        studentTabs.getTabs().addAll(
                marksTab,
                attendanceTab,
                analysisTab,
                refreshTab
        );

        studentTabs.setTabClosingPolicy(
                TabPane.TabClosingPolicy.UNAVAILABLE
        );

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> showLoginScreen());

        VBox layout = new VBox(
                15,
                heading,
                welcome,
                details,
                summaryCards,
                studentTabs,
                logoutButton
        );

        layout.setPadding(new Insets(20));

        layout.getStyleClass().add("modern-background");

        stage.setScene(createStyledScene(layout, 1150, 850));
    }

    // ---------------- STUDENT SUMMARY CARDS ----------------

    private HBox createStudentSummaryCards(Student student) {

        ArrayList<MarksRecord> studentMarks =
                getStudentMarks(student.getStudentId());

        double average = 0;

        if (!studentMarks.isEmpty()) {

            int total = 0;

            for (MarksRecord record : studentMarks) {
                total += record.getTotalMarks();
            }

            average = (double) total / studentMarks.size();
        }

        double attendance =
                calculateOverallAttendance(student.getStudentId());

        VBox subjectsCard = createCard(
                "Subjects",
                String.valueOf(studentMarks.size())
        );

        VBox averageCard = createCard(
                "Average Marks",
                String.format("%.2f%%", average)
        );

        VBox attendanceCard = createCard(
                "Attendance",
                String.format("%.2f%%", attendance)
        );

        VBox statusCard = createCard(
                "Attendance Status",
                attendanceRecordsForStudent(student.getStudentId())
                        ? getAttendanceStatus(attendance)
                        : "No Records"
        );

        HBox cards = new HBox(
                15,
                subjectsCard,
                averageCard,
                attendanceCard,
                statusCard
        );

        cards.setAlignment(Pos.CENTER);
        cards.setMaxWidth(Double.MAX_VALUE);

        return cards;
    }

    private boolean attendanceRecordsForStudent(String studentId) {

        return !getStudentAttendance(studentId).isEmpty();
    }

    // ---------------- STUDENT PERFORMANCE ANALYSIS ----------------

    private VBox createPerformanceAnalysis(Student student) {

        ArrayList<MarksRecord> studentMarks =
                getStudentMarks(student.getStudentId());

        Label title = new Label("My Performance Analysis");

        title.setStyle(
                "-fx-font-size: 21px; -fx-font-weight: bold;"
        );

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(25));

        if (studentMarks.isEmpty()) {

            layout.getChildren().addAll(
                    title,
                    new Label("No marks available for analysis yet.")
            );

            return layout;
        }

        int totalMarks = 0;
        int highestMarks = 0;
        int lowestMarks = 100;

        for (MarksRecord record : studentMarks) {

            int marks = record.getTotalMarks();

            totalMarks += marks;

            highestMarks = Math.max(highestMarks, marks);
            lowestMarks = Math.min(lowestMarks, marks);
        }

        double average =
                (double) totalMarks / studentMarks.size();

        String grade = calculateGrade(average);
        String status = calculatePerformanceStatus(average);

        Label subjects = new Label(
                "Subjects: " + studentMarks.size()
        );

        Label averageLabel = new Label(
                String.format("Average Marks: %.2f / 100", average)
        );

        Label highestLabel = new Label(
                "Highest Marks: " + highestMarks
        );

        Label lowestLabel = new Label(
                "Lowest Marks: " + lowestMarks
        );

        Label gradeLabel = new Label(
                "Overall Grade: " + grade
        );

        Label statusLabel = new Label(
                "Performance Status: " + status
        );

        ProgressBar progressBar = new ProgressBar(
                average / 100.0
        );

        progressBar.setPrefWidth(350);

        layout.getChildren().addAll(
                title,
                subjects,
                averageLabel,
                highestLabel,
                lowestLabel,
                gradeLabel,
                statusLabel,
                new Label("Average Performance"),
                progressBar
        );

        return layout;
    }

    // ---------------- CALCULATE GRADE AND STATUS ----------------

    private String calculateGrade(double average) {

        if (average >= 90) {
            return "A+";
        } else if (average >= 80) {
            return "A";
        } else if (average >= 70) {
            return "B";
        } else if (average >= 60) {
            return "C";
        } else if (average >= 50) {
            return "D";
        }

        return "F";
    }

    private String calculatePerformanceStatus(double average) {

        if (average >= 75) {
            return "Excellent";
        } else if (average >= 50) {
            return "Good";
        }

        return "Needs Improvement";
    }

    // ---------------- STYLED SCENE ----------------

    private Scene createStyledScene(
            Parent root,
            double width,
            double height) {

        Scene scene = new Scene(root, width, height);
        File cssFile = new File("src/styles.css");

        if (cssFile.exists()) {
            scene.getStylesheets().add(
                    cssFile.toURI().toString()
            );
        }

        return scene;
    }

    // ---------------- MAIN ----------------

    public static void main(String[] args) {
        launch(args);
    }
}