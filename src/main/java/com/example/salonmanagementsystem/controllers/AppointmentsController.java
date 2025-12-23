package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.model.Appointment;
import com.example.salonmanagementsystem.model.AppointmentStatus;
import com.example.salonmanagementsystem.model.Client;
import com.example.salonmanagementsystem.model.ServiceItem;
import com.example.salonmanagementsystem.model.Employee;
import com.example.salonmanagementsystem.service.AppointmentService;
import com.example.salonmanagementsystem.service.ClientService;
import com.example.salonmanagementsystem.service.ServiceService;
import com.example.salonmanagementsystem.service.EmployeeService;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.app.SessionContext;
import com.example.salonmanagementsystem.model.Role;
import com.example.salonmanagementsystem.model.User;
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

public class AppointmentsController {

    @FXML private TableView<Appointment> table;
    @FXML private TableColumn<Appointment, Long> idCol;
    @FXML private TableColumn<Appointment, String> startCol;
    @FXML private TableColumn<Appointment, String> endCol;
    @FXML private TableColumn<Appointment, String> statusCol;

    @FXML private ComboBox<Client> clientBox;
    @FXML private ComboBox<ServiceItem> serviceBox;
    @FXML private ComboBox<Employee> employeeBox;

    @FXML private DatePicker datePicker;
    @FXML private Spinner<Integer> hourSpinner;
    @FXML private Spinner<Integer> minuteSpinner;

    private final AppointmentService appointmentService = new AppointmentService();
    private final ClientService clientService = new ClientService();
    private final ServiceService serviceService = new ServiceService();
    private final EmployeeService employeeService = new EmployeeService();

    private final ObservableList<Appointment> data = FXCollections.observableArrayList();

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {

        /* ---------- TABLE SETUP ---------- */
        setupTable();

        /* ---------- TIME CONTROLS ---------- */
        setupTimeControls();

        /* ---------- COMBOBOX DATA ---------- */
        setupComboBoxes();

        /* ---------- LOAD DATA ---------- */
        reload();
    }

    private void setupTable() {
        idCol.setCellValueFactory(
                c -> new SimpleObjectProperty<>(c.getValue().getId())
        );

        startCol.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getStartTime().format(TIME_FORMATTER)
                )
        );

        endCol.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getEndTime().format(TIME_FORMATTER)
                )
        );

        statusCol.setCellValueFactory(
                c -> new SimpleStringProperty(
                        c.getValue().getStatus().name()
                )
        );

        table.setItems(data);
    }

    private void setupTimeControls() {
        // Часы: 8-20 (рабочее время салона)
        hourSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 20, 10)
        );

        // Минуты: с шагом 15 минут
        minuteSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 45, 0, 15)
        );

        // Установка начальной даты на сегодня
        datePicker.setValue(LocalDate.now());
    }

    private void setupComboBoxes() {
        // Загрузка клиентов
        try {
            ObservableList<Client> clients = FXCollections.observableArrayList(
                    clientService.getAllClients()
            );
            clientBox.setItems(clients);

            // Отображение имени клиента в ComboBox
            clientBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    if (empty || client == null) {
                        setText(null);
                    } else {
                        setText(client.getFirstName());
                    }
                }
            });

            clientBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    if (empty || client == null) {
                        setText(null);
                    } else {
                        setText(client.getFirstName());
                    }
                }
            });

        } catch (Exception e) {
            showError("Failed to load clients: " + e.getMessage());
        }

        // Загрузка услуг
        try {
            ObservableList<ServiceItem> services = FXCollections.observableArrayList(
                    serviceService.getAllServices()
            );
            serviceBox.setItems(services);

            // Отображение названия услуги в ComboBox
            serviceBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(ServiceItem service, boolean empty) {
                    super.updateItem(service, empty);
                    if (empty || service == null) {
                        setText(null);
                    } else {
                        setText(service.getName() + " (" + service.getDurationMinutes() + " min)");
                    }
                }
            });

            serviceBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(ServiceItem service, boolean empty) {
                    super.updateItem(service, empty);
                    if (empty || service == null) {
                        setText(null);
                    } else {
                        setText(service.getName() + " (" + service.getDurationMinutes() + " min)");
                    }
                }
            });

        } catch (Exception e) {
            showError("Failed to load services: " + e.getMessage());
        }

        // Загрузка сотрудников (только для ADMIN)
        User currentUser = SessionContext.getCurrentUser();
        if (currentUser != null && currentUser.getRole() == Role.ADMIN) {
            try {
                ObservableList<Employee> employees = FXCollections.observableArrayList(
                        employeeService.getAllEmployees()
                );

                if (employeeBox != null) {
                    employeeBox.setItems(employees);
                    employeeBox.setVisible(true);
                    employeeBox.setManaged(true);

                    // Отображение имени сотрудника в ComboBox
                    employeeBox.setButtonCell(new ListCell<>() {
                        @Override
                        protected void updateItem(Employee employee, boolean empty) {
                            super.updateItem(employee, empty);
                            if (empty || employee == null) {
                                setText(null);
                            } else {
                                setText(employee.getFirstName());
                            }
                        }
                    });

                    employeeBox.setCellFactory(lv -> new ListCell<>() {
                        @Override
                        protected void updateItem(Employee employee, boolean empty) {
                            super.updateItem(employee, empty);
                            if (empty || employee == null) {
                                setText(null);
                            } else {
                                setText(employee.getFirstName());
                            }
                        }
                    });
                }
            } catch (Exception e) {
                showError("Failed to load employees: " + e.getMessage());
            }
        } else {
            // Скрыть ComboBox сотрудников для MASTER
            if (employeeBox != null) {
                employeeBox.setVisible(false);
                employeeBox.setManaged(false);
            }
        }
    }

    private void reload() {
        try {
            data.setAll(appointmentService.getAppointments());
        } catch (Exception e) {
            showError("Failed to load appointments: " + e.getMessage());
        }
    }

    /* ==========================================================
       CREATE APPOINTMENT
       ========================================================== */

    @FXML
    private void onAdd() {
        try {
            // Валидация входных данных
            validateInput();

            // Создание объекта записи
            Appointment appointment = buildAppointmentFromForm();

            // Проверка на прошедшее время
            if (appointment.getStartTime().isBefore(LocalDateTime.now())) {
                showError("Cannot create appointment in the past");
                return;
            }

            // Создание записи (внутри проверяется конфликт времени)
            appointmentService.create(appointment);

            // Обновление таблицы
            reload();

            // Очистка формы
            clearForm();

            // Уведомление об успехе
            showSuccess("Appointment created successfully!");

        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            showError("Error creating appointment: " + e.getMessage());
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void validateInput() {
        StringBuilder errors = new StringBuilder();

        if (clientBox.getValue() == null) {
            errors.append("• Please select a client\n");
        }

        if (serviceBox.getValue() == null) {
            errors.append("• Please select a service\n");
        }

        // Проверка сотрудника только для ADMIN
        User currentUser = SessionContext.getCurrentUser();
        if (currentUser != null && currentUser.getRole() == Role.ADMIN) {
            if (employeeBox != null && employeeBox.getValue() == null) {
                errors.append("• Please select an employee\n");
            }
        }

        if (datePicker.getValue() == null) {
            errors.append("• Please select a date\n");
        }

        if (hourSpinner.getValue() == null) {
            errors.append("• Please select hour\n");
        }

        if (minuteSpinner.getValue() == null) {
            errors.append("• Please select minutes\n");
        }

        if (errors.length() > 0) {
            throw new ValidationException("Please fill all required fields:\n" + errors.toString());
        }
    }

    private Appointment buildAppointmentFromForm() {
        Client client = clientBox.getValue();
        ServiceItem service = serviceBox.getValue();
        LocalDate date = datePicker.getValue();
        int hour = hourSpinner.getValue();
        int minute = minuteSpinner.getValue();

        // Создание времени начала
        LocalDateTime startTime = LocalDateTime.of(date, LocalTime.of(hour, minute));

        // Вычисление времени окончания на основе длительности услуги
        LocalDateTime endTime = startTime.plusMinutes(service.getDurationMinutes());

        // Создание объекта записи
        Appointment appointment = new Appointment();
        appointment.setClientId(client.getId());
        appointment.setServiceId(service.getId());
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setStatus(AppointmentStatus.PLANNED);

        // Установка employeeId
        User currentUser = SessionContext.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getRole() == Role.ADMIN && employeeBox != null && employeeBox.getValue() != null) {
                // Для ADMIN берем выбранного сотрудника из ComboBox
                appointment.setEmployeeId(employeeBox.getValue().getId());
            } else if (currentUser.getRole() == Role.MASTER && currentUser.getEmployeeId() != null) {
                // Для MASTER устанавливается автоматически в сервисе, но можно установить здесь
                appointment.setEmployeeId(currentUser.getEmployeeId());
            } else {
                throw new ValidationException("Cannot determine employee for appointment");
            }
        }

        return appointment;
    }

    private void clearForm() {
        clientBox.setValue(null);
        serviceBox.setValue(null);
        if (employeeBox != null) {
            employeeBox.setValue(null);
        }
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
}