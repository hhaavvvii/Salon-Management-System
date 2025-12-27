package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.*;
import com.example.salonmanagementsystem.service.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AppointmentsController {

    @FXML private VBox calendarContainer;
    @FXML private DatePicker calendarDatePicker;
    @FXML private TextField searchClientField;
    @FXML private ComboBox<Employee> filterEmployeeBox;

    @FXML private ComboBox<Client> clientBox;
    @FXML private ComboBox<Service> serviceBox;
    @FXML private ComboBox<Employee> employeeBox;
    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;
    @FXML private Button createButton;
    @FXML private Label formTitle;

    private final AppointmentService appointmentService = new AppointmentService();
    private final ClientService clientService = new ClientService();
    private final ServiceService serviceService = new ServiceService();
    private final EmployeeService employeeService = new EmployeeService();

    private Appointment selectedAppointment = null;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        try {
            calendarDatePicker.setValue(LocalDate.now());
            datePicker.setValue(LocalDate.now());

            hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 20, 10));
            minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15));

            clientBox.setItems(FXCollections.observableArrayList(clientService.getAllClients()));
            serviceBox.setItems(FXCollections.observableArrayList(serviceService.getAllServices()));
            employeeBox.setItems(FXCollections.observableArrayList(employeeService.getAllEmployees()));
            filterEmployeeBox.setItems(FXCollections.observableArrayList(employeeService.getAllEmployees()));

            calendarDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> renderCalendar());
            searchClientField.textProperty().addListener((obs, oldText, newText) -> renderCalendar());
            filterEmployeeBox.valueProperty().addListener((obs, oldEmp, newEmp) -> renderCalendar());

            renderCalendar();

            if (SessionContext.isMaster()) {
                createButton.setDisable(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to initialize Appointments window: " + e.getMessage());
        }
    }

    private void renderCalendar() {
        calendarContainer.getChildren().clear();

        LocalDate selectedDate = calendarDatePicker.getValue();
        String clientQuery = searchClientField.getText() == null ? "" : searchClientField.getText().toLowerCase();
        Employee selectedEmployee = filterEmployeeBox.getValue();

        List<Appointment> appointments = appointmentService.getAllAppointments()
                .stream()
                .filter(a -> selectedDate == null || a.getStartTime().toLocalDate().equals(selectedDate))
                .filter(a -> clientQuery.isEmpty() || a.getClientName().toLowerCase().contains(clientQuery))
                .filter(a -> selectedEmployee == null || a.getEmployeeId().equals(selectedEmployee.getId()))
                .toList();

        for (int hour = 8; hour <= 20; hour++) {
            HBox row = new HBox(15);
            row.setMinHeight(80);
            row.setAlignment(Pos.TOP_LEFT);
            row.setPadding(new Insets(8, 0, 8, 0));

            // Time label
            Label time = new Label(String.format("%02d:00", hour));
            time.setPrefWidth(70);
            time.setStyle(
                    "-fx-font-weight: 600; " +
                            "-fx-text-fill: #6B7280; " +
                            "-fx-font-size: 14px;" +
                            "-fx-padding: 8 0 0 0;"
            );

            // Slot container
            VBox slot = new VBox(10);
            HBox.setHgrow(slot, Priority.ALWAYS);
            slot.setStyle(
                    "-fx-background-color: #F9FAFB; " +
                            "-fx-background-radius: 10; " +
                            "-fx-border-color: #E5E7EB; " +
                            "-fx-border-radius: 10; " +
                            "-fx-border-width: 1;"
            );
            slot.setPadding(new Insets(12));
            slot.setMinHeight(60);

            // Find appointments for this hour
            int finalHour = hour;
            List<Appointment> hourAppointments = appointments.stream()
                    .filter(a -> a.getStartTime().getHour() == finalHour)
                    .toList();

            if (hourAppointments.isEmpty()) {
                // Empty slot indicator
                Label emptyLabel = new Label("Available");
                emptyLabel.setStyle(
                        "-fx-text-fill: #9CA3AF; " +
                                "-fx-font-size: 12px; " +
                                "-fx-font-style: italic;"
                );
                slot.getChildren().add(emptyLabel);
            } else {
                // Add appointment cards
                for (Appointment a : hourAppointments) {
                    HBox card = createAppointmentCard(a);
                    slot.getChildren().add(card);
                }
            }

            row.getChildren().addAll(time, slot);
            calendarContainer.getChildren().add(row);
        }
    }

    private HBox createAppointmentCard(Appointment a) {
        HBox card = new HBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14));

        // Status color coding
        String cardColor = switch (a.getStatus()) {
            case PLANNED -> "#8B5CF6";
            case COMPLETED -> "#10B981";
            case CANCELLED -> "#EF4444";
        };

        card.setStyle(
                "-fx-background-color: " + cardColor + ";" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);" +
                        "-fx-cursor: hand;"
        );

        // Make card clickable
        card.setOnMouseClicked(event -> onAppointmentClick(a));

        // Info section
        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label clientName = new Label("👤 " + a.getClientName());
        clientName.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px;"
        );

        Label serviceName = new Label("✂️ " + a.getServiceName());
        serviceName.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.95); " +
                        "-fx-font-size: 13px;"
        );

        Label employeeName = new Label("👨‍💼 " + a.getEmployeeName());
        employeeName.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.9); " +
                        "-fx-font-size: 12px;"
        );

        info.getChildren().addAll(clientName, serviceName, employeeName);

        // Time and status section
        VBox timeStatus = new VBox(4);
        timeStatus.setAlignment(Pos.TOP_RIGHT);

        Label timeLabel = new Label(a.getStartTime().toLocalTime().toString());
        timeLabel.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-weight: 600; " +
                        "-fx-font-size: 13px;"
        );

        Label statusBadge = new Label(a.getStatus().name());
        statusBadge.setStyle(
                "-fx-background-color: rgba(255,255,255,0.3); " +
                        "-fx-text-fill: white; " +
                        "-fx-background-radius: 10; " +
                        "-fx-padding: 3 10; " +
                        "-fx-font-size: 10px; " +
                        "-fx-font-weight: 600;"
        );

        timeStatus.getChildren().addAll(timeLabel, statusBadge);

        card.getChildren().addAll(info, timeStatus);

        return card;
    }

    private void onAppointmentClick(Appointment appointment) {
        selectedAppointment = appointment;
        fillFormWithAppointment(appointment);
        setEditMode();
    }

    private void fillFormWithAppointment(Appointment appointment) {
        // Find and set client
        clientBox.getItems().stream()
                .filter(c -> c.getId().equals(appointment.getClientId()))
                .findFirst()
                .ifPresent(clientBox::setValue);

        // Find and set service
        serviceBox.getItems().stream()
                .filter(s -> s.getId().equals(appointment.getServiceId()))
                .findFirst()
                .ifPresent(serviceBox::setValue);

        // Find and set employee
        employeeBox.getItems().stream()
                .filter(e -> e.getId().equals(appointment.getEmployeeId()))
                .findFirst()
                .ifPresent(employeeBox::setValue);

        // Set date and time
        datePicker.setValue(appointment.getStartTime().toLocalDate());
        hourSpinner.getValueFactory().setValue(appointment.getStartTime().getHour());
        minuteSpinner.getValueFactory().setValue(appointment.getStartTime().getMinute());
    }

    @FXML
    private void onSearch() {
        renderCalendar();
    }

    @FXML
    private void onReset() {
        searchClientField.clear();
        filterEmployeeBox.setValue(null);
        calendarDatePicker.setValue(LocalDate.now());
        renderCalendar();
    }

    @FXML
    private void onAdd() {
        if (SessionContext.isMaster()) {
            showError("Access denied: masters cannot create/edit appointments");
            return;
        }

        try {
            Service s = serviceBox.getValue();
            Client c = clientBox.getValue();
            Employee e = employeeBox.getValue();
            LocalDate date = datePicker.getValue();

            if (c == null || s == null || e == null || date == null) {
                showError("Please fill all required fields");
                return;
            }

            LocalDateTime start = LocalDateTime.of(date, LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue()));
            LocalDateTime end = start.plusMinutes(s.getDurationMinutes());

            if (isEditMode && selectedAppointment != null) {
                // Edit mode
                selectedAppointment.setClientId(c.getId());
                selectedAppointment.setServiceId(s.getId());
                selectedAppointment.setEmployeeId(e.getId());
                selectedAppointment.setStartTime(start);
                selectedAppointment.setEndTime(end);

                appointmentService.updateAppointment(selectedAppointment);
                showSuccess("Appointment updated successfully!");
            } else {
                // Create mode
                Appointment a = new Appointment();
                a.setClientId(c.getId());
                a.setServiceId(s.getId());
                a.setEmployeeId(e.getId());
                a.setStartTime(start);
                a.setEndTime(end);
                a.setStatus(AppointmentStatus.PLANNED);
                a.setPrice(s.getPrice());
                a.setDurationMinutes(s.getDurationMinutes());

                appointmentService.createAppointment(a);
                showSuccess("Appointment created successfully!");
            }

            renderCalendar();
            onClear();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Failed to save appointment: " + ex.getMessage());
        }
    }

    @FXML
    private void onClear() {
        clearForm();
        setAddMode();
    }

    @FXML
    private void onCancel() {
        if (selectedAppointment == null) {
            showWarning("Please select an appointment first");
            return;
        }

        if (selectedAppointment.getStatus() == AppointmentStatus.CANCELLED) {
            showWarning("Appointment is already cancelled");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancel Appointment");
        confirmation.setHeaderText("Are you sure?");
        confirmation.setContentText("Do you want to cancel this appointment?");

        DialogPane dialogPane = confirmation.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    appointmentService.updateStatus(selectedAppointment.getId(), AppointmentStatus.CANCELLED);
                    showSuccess("Appointment cancelled successfully");
                    renderCalendar();
                    onClear();
                } catch (Exception e) {
                    showError("Failed to cancel appointment: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onComplete() {
        if (selectedAppointment == null) {
            showWarning("Please select an appointment first");
            return;
        }

        if (selectedAppointment.getStatus() == AppointmentStatus.COMPLETED) {
            showWarning("Appointment is already completed");
            return;
        }

        if (selectedAppointment.getStatus() == AppointmentStatus.CANCELLED) {
            showWarning("Cannot complete a cancelled appointment");
            return;
        }

        try {
            appointmentService.updateStatus(selectedAppointment.getId(), AppointmentStatus.COMPLETED);
            showSuccess("Appointment marked as completed");
            renderCalendar();
            onClear();
        } catch (Exception e) {
            showError("Failed to complete appointment: " + e.getMessage());
        }
    }

    private void clearForm() {
        clientBox.setValue(null);
        serviceBox.setValue(null);
        employeeBox.setValue(null);
        datePicker.setValue(LocalDate.now());
        hourSpinner.getValueFactory().setValue(10);
        minuteSpinner.getValueFactory().setValue(0);
        selectedAppointment = null;
    }

    private void setAddMode() {
        isEditMode = false;
        selectedAppointment = null;

        if (formTitle != null) {
            formTitle.setText("Create Appointment");
        }

        if (createButton != null) {
            createButton.setText("Create Appointment");
            createButton.setStyle(
                    "-fx-background-color: #8B5CF6; -fx-text-fill: white; " +
                            "-fx-background-radius: 8; -fx-padding: 14 24; " +
                            "-fx-font-size: 14px; -fx-cursor: hand; -fx-font-weight: 600;"
            );
        }
    }

    private void setEditMode() {
        isEditMode = true;

        if (formTitle != null) {
            formTitle.setText("Edit Appointment");
        }

        if (createButton != null) {
            createButton.setText("Update Appointment");
            createButton.setStyle(
                    "-fx-background-color: #F59E0B; -fx-text-fill: white; " +
                            "-fx-background-radius: 8; -fx-padding: 14 24; " +
                            "-fx-font-size: 14px; -fx-cursor: hand; -fx-font-weight: 600;"
            );
        }
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", "Operation Failed", message);
    }

    private void showSuccess(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Success", "Operation Completed", message);
    }

    private void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "Warning", "Attention Required", message);
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Modern alert styling
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: white; " +
                        "-fx-font-family: 'Segoe UI', sans-serif;"
        );

        alert.showAndWait();
    }
}