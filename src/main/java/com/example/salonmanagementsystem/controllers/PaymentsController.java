package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.*;
import com.example.salonmanagementsystem.service.PaymentService;
import com.example.salonmanagementsystem.service.AppointmentService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaymentsController {

    @FXML private TableView<Payment> paymentsTable;
    @FXML private TableColumn<Payment, String> idCol;
    @FXML private TableColumn<Payment, String> dateCol;
    @FXML private TableColumn<Payment, String> clientCol;
    @FXML private TableColumn<Payment, String> employeeCol;
    @FXML private TableColumn<Payment, String> serviceCol;
    @FXML private TableColumn<Payment, String> amountCol;
    @FXML private TableColumn<Payment, String> methodCol;
    @FXML private TableColumn<Payment, String> statusCol;

    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private ComboBox<String> methodFilter;
    @FXML private ComboBox<String> statusFilter;

    @FXML private ComboBox<Appointment> appointmentBox;
    @FXML private TextField clientField;
    @FXML private TextField serviceField;
    @FXML private TextField amountField;
    @FXML private ComboBox<PaymentMethod> methodBox;
    @FXML private DatePicker paymentDatePicker;
    @FXML private TextArea commentField;

    private final PaymentService paymentService = new PaymentService();
    private final AppointmentService appointmentService = new AppointmentService();

    private final ObservableList<Payment> paymentData = FXCollections.observableArrayList();
    private Payment selectedPayment = null;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        setupForm();
        loadData();
    }

    private void setupTable() {
        idCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.valueOf(data.getValue().getId()))
        );

        dateCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPaymentDate().format(DATE_FORMATTER))
        );

        clientCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getClientName())
        );

        employeeCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEmployeeName())
        );

        serviceCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getServiceName())
        );

        amountCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f", data.getValue().getAmount()))
        );

        methodCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPaymentMethod().getDisplayName())
        );

        statusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus().getDisplayName())
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
                    if (status.equals("Paid")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if (status.equals("Refunded")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });

        paymentsTable.setItems(paymentData);

        paymentsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    selectedPayment = newSelection;
                }
        );
    }

    private void setupFilters() {
        methodFilter.setItems(FXCollections.observableArrayList(
                "ALL", "Cash", "Card", "Transfer"
        ));
        methodFilter.setValue("ALL");

        statusFilter.setItems(FXCollections.observableArrayList(
                "ALL", "Paid", "Refunded"
        ));
        statusFilter.setValue("ALL");
    }

    private void setupForm() {
        // Payment methods
        methodBox.setItems(FXCollections.observableArrayList(PaymentMethod.values()));

        // Default date
        paymentDatePicker.setValue(LocalDate.now());

        // Load completed appointments without payment
        loadCompletedAppointments();

        // Auto-fill fields when appointment is selected
        appointmentBox.setOnAction(e -> fillFormFromAppointment());
    }

    private void loadCompletedAppointments() {
        try {
            List<Appointment> allAppointments = appointmentService.getAppointments();
            List<Appointment> completed = allAppointments.stream()
                    .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                    .filter(a -> !paymentService.isAppointmentPaid(a.getId()))
                    .toList();

            appointmentBox.setItems(FXCollections.observableArrayList(completed));

            appointmentBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Appointment appointment, boolean empty) {
                    super.updateItem(appointment, empty);
                    if (empty || appointment == null) {
                        setText(null);
                    } else {
                        setText(appointment.getClientName() + " - " + appointment.getServiceName());
                    }
                }
            });

            appointmentBox.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(Appointment appointment, boolean empty) {
                    super.updateItem(appointment, empty);
                    if (empty || appointment == null) {
                        setText(null);
                    } else {
                        setText(appointment.getClientName() + " - " + appointment.getServiceName());
                    }
                }
            });

        } catch (Exception e) {
            showError("Failed to load appointments: " + e.getMessage());
        }
    }

    private void fillFormFromAppointment() {
        Appointment selected = appointmentBox.getValue();
        if (selected != null) {
            clientField.setText(selected.getClientName());
            serviceField.setText(selected.getServiceName());
            amountField.setText(String.format("%.2f", selected.getPrice()));
        }
    }

    private void loadData() {
        try {
            paymentData.setAll(paymentService.getAllPayments());
        } catch (Exception e) {
            showError("Failed to load payments: " + e.getMessage());
        }
    }

    @FXML
    private void onCreate() {
        try {
            Appointment appointment = appointmentBox.getValue();
            PaymentMethod method = methodBox.getValue();
            LocalDate date = paymentDatePicker.getValue();

            if (appointment == null || method == null || date == null) {
                throw new ValidationException("Please fill all required fields");
            }

            Payment payment = new Payment();
            payment.setAppointmentId(appointment.getId());
            payment.setAmount(appointment.getPrice());
            payment.setPaymentMethod(method);
            payment.setPaymentDate(date);
            payment.setComment(commentField.getText().trim());

            paymentService.createPayment(payment);
            loadData();
            loadCompletedAppointments();
            onClear();
            showSuccess("Payment created successfully!");

        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error creating payment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onRefund() {
        if (selectedPayment == null) {
            showWarning("Please select a payment to refund");
            return;
        }

        if (selectedPayment.getStatus() == PaymentStatus.REFUNDED) {
            showWarning("Payment is already refunded");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Refund Payment");
        dialog.setHeaderText("Refund Payment");
        dialog.setContentText("Reason for refund:");

        dialog.showAndWait().ifPresent(reason -> {
            try {
                paymentService.refundPayment(selectedPayment.getId(), reason);
                loadData();
                showSuccess("Payment refunded successfully!");
            } catch (Exception e) {
                showError("Error refunding payment: " + e.getMessage());
            }
        });
    }

    @FXML
    private void onSearch() {
        try {
            List<Payment> allPayments = paymentService.getAllPayments();
            List<Payment> filtered = allPayments;

            // Filter by date range
            LocalDate from = fromDatePicker.getValue();
            LocalDate to = toDatePicker.getValue();
            if (from != null && to != null) {
                filtered = filtered.stream()
                        .filter(p -> !p.getPaymentDate().isBefore(from) && !p.getPaymentDate().isAfter(to))
                        .toList();
            }

            // Filter by method
            String method = methodFilter.getValue();
            if (method != null && !method.equals("ALL")) {
                filtered = filtered.stream()
                        .filter(p -> p.getPaymentMethod().getDisplayName().equals(method))
                        .toList();
            }

            // Filter by status
            String status = statusFilter.getValue();
            if (status != null && !status.equals("ALL")) {
                filtered = filtered.stream()
                        .filter(p -> p.getStatus().getDisplayName().equals(status))
                        .toList();
            }

            paymentData.setAll(filtered);

        } catch (Exception e) {
            showError("Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void onReset() {
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        methodFilter.setValue("ALL");
        statusFilter.setValue("ALL");
        loadData();
    }

    @FXML
    private void onClear() {
        appointmentBox.setValue(null);
        clientField.clear();
        serviceField.clear();
        amountField.clear();
        methodBox.setValue(null);
        paymentDatePicker.setValue(LocalDate.now());
        commentField.clear();
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
}