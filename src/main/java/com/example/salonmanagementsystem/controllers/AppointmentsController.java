package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.model.Appointment;
import com.example.salonmanagementsystem.model.AppointmentStatus;
import com.example.salonmanagementsystem.model.Client;
import com.example.salonmanagementsystem.model.Service;
import com.example.salonmanagementsystem.model.Employee;
import com.example.salonmanagementsystem.service.AppointmentService;
import com.example.salonmanagementsystem.service.ClientService;
import com.example.salonmanagementsystem.service.ServiceService;
import com.example.salonmanagementsystem.service.EmployeeService;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentsController {

    @FXML private TableView<Appointment> table;
    @FXML private TableColumn<Appointment, Long> idCol;
    @FXML private TableColumn<Appointment, String> dateCol;
    @FXML private TableColumn<Appointment, String> timeCol;
    @FXML private TableColumn<Appointment, String> clientCol;
    @FXML private TableColumn<Appointment, String> employeeCol;
    @FXML private TableColumn<Appointment, String> serviceCol;
    @FXML private TableColumn<Appointment, String> durationCol;
    @FXML private TableColumn<Appointment, String> priceCol;
    @FXML private TableColumn<Appointment, String> statusCol;

    @FXML private ComboBox<Client> clientBox;
    @FXML private ComboBox<Service> serviceBox;
    @FXML private ComboBox<Employee> employeeBox;

    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;

    @FXML private DatePicker filterDatePicker;
    @FXML private ComboBox<Employee> filterEmployeeBox;
    @FXML private ComboBox<String> filterStatusBox;

    @FXML private Label formTitle;
    @FXML private Button createButton;

    private final AppointmentService appointmentService = new AppointmentService();
    private final ClientService clientService = new ClientService();
    private final ServiceService serviceService = new ServiceService();
    private final EmployeeService employeeService = new EmployeeService();

    private final ObservableList<Appointment> data = FXCollections.observableArrayList();
    private Appointment selectedAppointment = null;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        setupTable();
        setupTimeControls();
        setupFilters();
        setupComboBoxes();
        reload();
    }

    private void setupTable() {
        idCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));

        dateCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStartTime().format(DATE_FORMATTER))
        );

        timeCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStartTime().format(TIME_FORMATTER))
        );

        clientCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getClientName())
        );

        employeeCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEmployeeName())
        );

        serviceCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getServiceName())
        );

        durationCol.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getDurationMinutes()))
        );

        priceCol.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.2f", c.getValue().getPrice()))
        );

        statusCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatus().getDisplayName())
        );

        // Цветовое выделение статуса
        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("Planned")) {
                        setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                    } else if (status.equals("Completed")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if (status.equals("Canceled")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });

        table.setItems(data);

        // Обработка выбора строки
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedAppointment = newSelection;
                }
        );
    }

    private void setupTimeControls() {
        hourSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 20, 10)
        );

        minuteSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15)
        );

        datePicker.setValue(LocalDate.now());
    }

    private void setupFilters() {
        // Статусы для фильтра
        filterStatusBox.setItems(FXCollections.observableArrayList(
                "ALL", "Planned", "Completed", "Canceled"
        ));
        filterStatusBox.setValue("ALL");

        // Сотрудники для фильтра
        try {
            ObservableList<Employee> allEmployees = FXCollections.observableArrayList();
            allEmployees.add(null); // "All employees"
            allEmployees.addAll(employeeService.getAllEmployees());
            filterEmployeeBox.setItems(allEmployees);

            filterEmployeeBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Employee employee, boolean empty) {
                    super.updateItem(employee, empty);
                    setText(empty || employee == null ? "All employees" : employee.getFullName());
                }
            });

            filterEmployeeBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Employee employee, boolean empty) {
                    super.updateItem(employee, empty);
                    setText(empty || employee == null ? "All employees" : employee.getFullName());
                }
            });
        } catch (Exception e) {
            showError("Failed to load employees for filter: " + e.getMessage());
        }
    }

    private void setupComboBoxes() {
        // Клиенты
        try {
            List<Client> clients = clientService.getActiveClients();
            clientBox.setItems(FXCollections.observableArrayList(clients));

            clientBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    setText(empty || client == null ? null : client.getFullName());
                }
            });

            clientBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    setText(empty || client == null ? null : client.getFullName());
                }
            });
        } catch (Exception e) {
            showError("Failed to load clients: " + e.getMessage());
        }

        // Услуги
        try {
            List<Service> services = serviceService.getActiveServices();
            serviceBox.setItems(FXCollections.observableArrayList(services));

            serviceBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Service service, boolean empty) {
                    super.updateItem(service, empty);
                    setText(empty || service == null ? null : service.getName());
                }
            });

            serviceBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Service service, boolean empty) {
                    super.updateItem(service, empty);
                    setText(empty || service == null ? null : service.getName());
                }
            });
        } catch (Exception e) {
            showError("Failed to load services: " + e.getMessage());
        }

        // Сотрудники
        try {
            List<Employee> employees = employeeService.getActiveEmployees();
            employeeBox.setItems(FXCollections.observableArrayList(employees));

            employeeBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Employee employee, boolean empty) {
                    super.updateItem(employee, empty);
                    setText(empty || employee == null ? null : employee.getFullName());
                }
            });

            employeeBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Employee employee, boolean empty) {
                    super.updateItem(employee, empty);
                    setText(empty || employee == null ? null : employee.getFullName());
                }
            });
        } catch (Exception e) {
            showError("Failed to load employees: " + e.getMessage());
        }
    }

    private void reload() {
        try {
            data.setAll(appointmentService.getAppointments());
        } catch (Exception e) {
            showError("Failed to load appointments: " + e.getMessage());
        }
    }

    @FXML
    private void onAdd() {
        try {
            Appointment a = buildAppointmentFromForm();
            appointmentService.create(a);
            reload();
            onClear();
            showSuccess("Appointment created successfully!");
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private Appointment buildAppointmentFromForm() {
        Client client = clientBox.getValue();
        Service service = serviceBox.getValue();
        Employee employee = employeeBox.getValue();
        LocalDate date = datePicker.getValue();

        if (client == null || service == null || employee == null || date == null) {
            throw new ValidationException("Please fill all required fields");
        }

        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();

        LocalDateTime start = LocalDateTime.of(date, LocalTime.of(hour, minute));
        LocalDateTime end = start.plusMinutes(service.getDurationMinutes());

        Appointment a = new Appointment();
        a.setClientId(client.getId());
        a.setServiceId(service.getId());
        a.setEmployeeId(employee.getId());
        a.setStartTime(start);
        a.setEndTime(end);
        a.setStatus(AppointmentStatus.PLANNED);
        a.setPrice(service.getPrice());
        a.setDurationMinutes(service.getDurationMinutes());

        return a;
    }

    @FXML
    private void onComplete() {
        if (selectedAppointment == null) {
            showWarning("Please select an appointment");
            return;
        }

        if (selectedAppointment.getStatus() == AppointmentStatus.COMPLETED) {
            showWarning("Appointment is already completed");
            return;
        }

        if (selectedAppointment.getStatus() == AppointmentStatus.CANCELED) {
            showWarning("Cannot complete canceled appointment");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Complete Appointment");
        confirmation.setHeaderText("Mark as Completed");
        confirmation.setContentText("Mark this appointment as completed?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    appointmentService.completeAppointment(selectedAppointment.getId());
                    reload();
                    showSuccess("Appointment completed!");
                } catch (Exception e) {
                    showError("Error completing appointment: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onCancel() {
        if (selectedAppointment == null) {
            showWarning("Please select an appointment");
            return;
        }

        if (selectedAppointment.getStatus() == AppointmentStatus.CANCELED) {
            showWarning("Appointment is already canceled");
            return;
        }

        if (selectedAppointment.getStatus() == AppointmentStatus.COMPLETED) {
            showWarning("Cannot cancel completed appointment");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Cancel Appointment");
        confirmation.setHeaderText("Cancel Appointment");
        confirmation.setContentText("Are you sure you want to cancel this appointment?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    appointmentService.cancelAppointment(selectedAppointment.getId());
                    reload();
                    showSuccess("Appointment canceled!");
                } catch (Exception e) {
                    showError("Error canceling appointment: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onSearch() {
        try {
            List<Appointment> allAppointments = appointmentService.getAppointments();
            List<Appointment> filtered = allAppointments;

            // Фильтр по дате
            LocalDate selectedDate = filterDatePicker.getValue();
            if (selectedDate != null) {
                filtered = filtered.stream()
                        .filter(a -> a.getStartTime().toLocalDate().equals(selectedDate))
                        .toList();
            }

            // Фильтр по сотруднику
            Employee selectedEmployee = filterEmployeeBox.getValue();
            if (selectedEmployee != null) {
                filtered = filtered.stream()
                        .filter(a -> a.getEmployeeId().equals(selectedEmployee.getId()))
                        .toList();
            }

            // Фильтр по статусу
            String selectedStatus = filterStatusBox.getValue();
            if (selectedStatus != null && !selectedStatus.equals("ALL")) {
                filtered = filtered.stream()
                        .filter(a -> a.getStatus().getDisplayName().equals(selectedStatus))
                        .toList();
            }

            data.setAll(filtered);

        } catch (Exception e) {
            showError("Search failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onResetFilter() {
        filterDatePicker.setValue(null);
        filterEmployeeBox.setValue(null);
        filterStatusBox.setValue("ALL");
        reload();
    }

    @FXML
    private void onClear() {
        clientBox.setValue(null);
        serviceBox.setValue(null);
        employeeBox.setValue(null);
        datePicker.setValue(LocalDate.now());
        hourSpinner.getValueFactory().setValue(10);
        minuteSpinner.getValueFactory().setValue(0);
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

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Attention Required");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}