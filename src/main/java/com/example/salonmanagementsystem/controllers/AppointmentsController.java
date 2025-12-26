package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.*;
import com.example.salonmanagementsystem.service.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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

    private final AppointmentService appointmentService = new AppointmentService();
    private final ClientService clientService = new ClientService();
    private final ServiceService serviceService = new ServiceService();
    private final EmployeeService employeeService = new EmployeeService();

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
            row.setMinHeight(70);

            Label time = new Label(String.format("%02d:00", hour));
            time.setPrefWidth(60);
            time.setStyle("-fx-font-weight: bold; -fx-text-fill: #6f42c1;");

            VBox slot = new VBox(8);
            slot.setPrefWidth(850);
            slot.setStyle("-fx-background-color: #ede7f6; -fx-background-radius: 12;");
            slot.setPadding(new Insets(8));

            for (Appointment a : appointments) {
                if (a.getStartTime().getHour() == hour) {
                    VBox card = new VBox(4);
                    card.setPadding(new Insets(10));
                    card.setStyle("""
                        -fx-background-color: #6f42c1;
                        -fx-background-radius: 14;
                        -fx-border-color: #6f42c1;
                        -fx-border-radius: 14;
                        """);

                    card.getChildren().addAll(
                            new Label(a.getClientName()),
                            new Label(a.getServiceName()),
                            new Label(a.getEmployeeName()),
                            new Label(a.getStartTime().toLocalTime() + " • " + a.getStatus().name())
                    );

                    slot.getChildren().add(card);
                }
            }

            row.getChildren().addAll(time, slot);
            calendarContainer.getChildren().add(row);
        }
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
            showError("Access denied: masters cannot create appointments");
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

            Appointment a = new Appointment();
            a.setClientId(c.getId());
            a.setServiceId(s.getId());
            a.setEmployeeId(e.getId());
            a.setStartTime(start);
            a.setEndTime(start.plusMinutes(s.getDurationMinutes()));
            a.setStatus(AppointmentStatus.PLANNED);
            a.setPrice(s.getPrice());
            a.setDurationMinutes(s.getDurationMinutes());

            appointmentService.createAppointment(a);
            renderCalendar();
            showSuccess("Appointment created!");
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Failed to create appointment: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation Failed");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Operation Completed");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
