package com.example.salonmanagementsystem.controllers;

import com.example.salonmanagementsystem.dto.ClientActivityReportRow;
import com.example.salonmanagementsystem.dto.EmployeeLoadReportRow;
import com.example.salonmanagementsystem.dto.RevenueReportRow;
import com.example.salonmanagementsystem.dto.ServiceReportRow;
import com.example.salonmanagementsystem.exceptions.ValidationException;
import com.example.salonmanagementsystem.model.Employee;
import com.example.salonmanagementsystem.model.Service;
import com.example.salonmanagementsystem.service.EmployeeService;
import com.example.salonmanagementsystem.service.ReportService;
import com.example.salonmanagementsystem.service.ServiceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class ReportsController {

    // Services
    private final ReportService reportService = new ReportService();
    private final EmployeeService employeeService = new EmployeeService();
    private final ServiceService serviceItemService = new ServiceService();

    // Period Filters
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private Label totalRevenueLabel;
    @FXML private Label statusLabel;
    @FXML private TabPane reportsTabPane;

    // Revenue Tab
    @FXML private TableView<RevenueReportRow> revenueTable;
    @FXML private TableColumn<RevenueReportRow, LocalDate> revDateCol;
    @FXML private TableColumn<RevenueReportRow, String> revEmployeeCol;
    @FXML private TableColumn<RevenueReportRow, String> revServiceCol;
    @FXML private TableColumn<RevenueReportRow, Integer> revCountCol;
    @FXML private TableColumn<RevenueReportRow, Double> revTotalCol;
    @FXML private ComboBox<Employee> employeeFilterBox;
    @FXML private ComboBox<Service> serviceFilterBox;

    // Employee Load Tab
    @FXML private TableView<EmployeeLoadReportRow> employeeLoadTable;
    @FXML private TableColumn<EmployeeLoadReportRow, String> empNameCol;
    @FXML private TableColumn<EmployeeLoadReportRow, Integer> empAppointmentsCol;
    @FXML private TableColumn<EmployeeLoadReportRow, String> empWorkTimeCol;
    @FXML private TableColumn<EmployeeLoadReportRow, Double> empRevenueCol;
    @FXML private TableColumn<EmployeeLoadReportRow, Double> empLoadCol;

    // Client Activity Tab
    @FXML private TableView<ClientActivityReportRow> clientActivityTable;
    @FXML private TableColumn<ClientActivityReportRow, String> clientNameCol;
    @FXML private TableColumn<ClientActivityReportRow, String> clientPhoneCol;
    @FXML private TableColumn<ClientActivityReportRow, Integer> clientVisitsCol;
    @FXML private TableColumn<ClientActivityReportRow, Double> clientSpentCol;
    @FXML private TableColumn<ClientActivityReportRow, String> clientLastVisitCol;

    // Services Tab
    @FXML private TableView<ServiceReportRow> servicesTable;
    @FXML private TableColumn<ServiceReportRow, String> svcNameCol;
    @FXML private TableColumn<ServiceReportRow, Integer> svcBookedCol;
    @FXML private TableColumn<ServiceReportRow, Double> svcRevenueCol;
    @FXML private TableColumn<ServiceReportRow, Double> svcAvgPriceCol;

    @FXML
    public void initialize() {
        setupTables();
        loadFilters();
        setDefaultDates();
        updateStatus("Ready. Select period and click 'Generate Report'");
    }

    private void setupTables() {
        // Modern table styling
        String tableStyle = "-fx-background-color: white; -fx-border-width: 0;";
        revenueTable.setStyle(tableStyle);
        employeeLoadTable.setStyle(tableStyle);
        clientActivityTable.setStyle(tableStyle);
        servicesTable.setStyle(tableStyle);

        // Revenue Table
        revDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        revEmployeeCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        revServiceCol.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        revCountCol.setCellValueFactory(new PropertyValueFactory<>("completedCount"));
        revTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        formatMoneyColumn(revTotalCol);
        revCountCol.setStyle("-fx-alignment: CENTER;");

        // Employee Load Table
        empNameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        empAppointmentsCol.setCellValueFactory(new PropertyValueFactory<>("totalAppointments"));
        empWorkTimeCol.setCellValueFactory(new PropertyValueFactory<>("formattedWorkTime"));
        empRevenueCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        empLoadCol.setCellValueFactory(new PropertyValueFactory<>("loadPercentage"));
        formatMoneyColumn(empRevenueCol);
        formatPercentColumn(empLoadCol);
        empAppointmentsCol.setStyle("-fx-alignment: CENTER;");

        // Client Activity Table
        clientNameCol.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        clientPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        clientVisitsCol.setCellValueFactory(new PropertyValueFactory<>("totalVisits"));
        clientSpentCol.setCellValueFactory(new PropertyValueFactory<>("totalSpent"));
        clientLastVisitCol.setCellValueFactory(new PropertyValueFactory<>("lastVisitDate"));
        formatMoneyColumn(clientSpentCol);
        clientVisitsCol.setStyle("-fx-alignment: CENTER;");

        // Services Table
        svcNameCol.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        svcBookedCol.setCellValueFactory(new PropertyValueFactory<>("timesBooked"));
        svcRevenueCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        svcAvgPriceCol.setCellValueFactory(new PropertyValueFactory<>("averagePrice"));
        formatMoneyColumn(svcRevenueCol);
        formatMoneyColumn(svcAvgPriceCol);
        svcBookedCol.setStyle("-fx-alignment: CENTER;");
    }

    private void loadFilters() {
        // Load employees
        List<Employee> employees = employeeService.getAllEmployees();
        ObservableList<Employee> employeeList = FXCollections.observableArrayList(employees);
        employeeFilterBox.setItems(employeeList);
        employeeFilterBox.setPromptText("All employees");

        // Load services
        List<Service> services = serviceItemService.getAllServices();
        ObservableList<Service> serviceList = FXCollections.observableArrayList(services);
        serviceFilterBox.setItems(serviceList);
        serviceFilterBox.setPromptText("All services");
    }

    private void setDefaultDates() {
        LocalDate now = LocalDate.now();
        fromDatePicker.setValue(now.withDayOfMonth(1));
        toDatePicker.setValue(now);
    }

    @FXML
    private void onGenerate() {
        try {
            LocalDate from = fromDatePicker.getValue();
            LocalDate to = toDatePicker.getValue();

            if (from == null || to == null) {
                showError("Please select both start and end dates");
                return;
            }

            if (from.isAfter(to)) {
                showError("Start date cannot be after end date");
                return;
            }

            // Generate all reports
            generateAllReports(from, to);

            // Update total revenue
            Double totalRevenue = reportService.getTotalRevenue(from, to);
            totalRevenueLabel.setText(String.format("%.2f ₸", totalRevenue));

            updateStatus("✓ Reports generated for period: " + from + " to " + to);

        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error generating reports: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generateAllReports(LocalDate from, LocalDate to) {
        // Revenue
        List<RevenueReportRow> revenueData = reportService.generateRevenueReport(from, to, null, null);
        revenueTable.setItems(FXCollections.observableArrayList(revenueData));

        // Employee Load
        List<EmployeeLoadReportRow> employeeData = reportService.generateEmployeeLoadReport(from, to);
        employeeLoadTable.setItems(FXCollections.observableArrayList(employeeData));

        // Client Activity
        List<ClientActivityReportRow> clientData = reportService.generateClientActivityReport(from, to);
        clientActivityTable.setItems(FXCollections.observableArrayList(clientData));

        // Services
        List<ServiceReportRow> serviceData = reportService.generateServiceStatistics(from, to);
        servicesTable.setItems(FXCollections.observableArrayList(serviceData));
    }

    @FXML
    private void onApplyRevenueFilters() {
        try {
            LocalDate from = fromDatePicker.getValue();
            LocalDate to = toDatePicker.getValue();

            if (from == null || to == null) {
                showError("Please select period first");
                return;
            }

            Long employeeId = employeeFilterBox.getValue() != null
                    ? employeeFilterBox.getValue().getId() : null;
            Long serviceId = serviceFilterBox.getValue() != null
                    ? serviceFilterBox.getValue().getId() : null;

            List<RevenueReportRow> data = reportService.generateRevenueReport(from, to, employeeId, serviceId);
            revenueTable.setItems(FXCollections.observableArrayList(data));

            updateStatus("✓ Revenue filters applied");

        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error applying filters: " + e.getMessage());
        }
    }

    @FXML
    private void onExport() {
        try {
            Tab selectedTab = reportsTabPane.getSelectionModel().getSelectedItem();
            String tabName = selectedTab.getText().replaceAll("[^a-zA-Z\\s]", "").trim(); // Remove emojis

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Report");
            fileChooser.setInitialFileName("report_" + tabName.toLowerCase().replace(" ", "_") + ".csv");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );

            File file = fileChooser.showSaveDialog(reportsTabPane.getScene().getWindow());
            if (file != null) {
                exportCurrentTab(tabName, file.getAbsolutePath());
                showInfo("✓ Report exported successfully to: " + file.getName());
            }

        } catch (Exception e) {
            showError("Error exporting report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void exportCurrentTab(String tabName, String filepath) throws Exception {
        switch (tabName) {
            case "Revenue" -> {
                List<RevenueReportRow> data = revenueTable.getItems();
                reportService.exportRevenueReport(data, filepath);
            }
            case "Employee Load" -> {
                List<EmployeeLoadReportRow> data = employeeLoadTable.getItems();
                reportService.exportEmployeeLoadReport(data, filepath);
            }
            case "Client Activity" -> {
                List<ClientActivityReportRow> data = clientActivityTable.getItems();
                reportService.exportClientActivityReport(data, filepath);
            }
            case "Services" -> {
                List<ServiceReportRow> data = servicesTable.getItems();
                reportService.exportServiceStatistics(data, filepath);
            }
        }
    }

    @FXML
    private void onReset() {
        setDefaultDates();
        employeeFilterBox.setValue(null);
        serviceFilterBox.setValue(null);
        revenueTable.getItems().clear();
        employeeLoadTable.getItems().clear();
        clientActivityTable.getItems().clear();
        servicesTable.getItems().clear();
        totalRevenueLabel.setText("0.00 ₸");
        updateStatus("Filters reset");
    }

    private <T> void formatMoneyColumn(TableColumn<T, Double> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f ₸", amount));
                    setStyle("-fx-font-weight: 600; -fx-text-fill: #059669;");
                }
            }
        });
    }

    private <T> void formatPercentColumn(TableColumn<T, Double> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double percent, boolean empty) {
                super.updateItem(percent, empty);
                if (empty || percent == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.1f%%", percent));

                    // Color coding based on load percentage
                    if (percent >= 80) {
                        setStyle("-fx-text-fill: #DC2626; -fx-font-weight: 600;"); // High load - red
                    } else if (percent >= 60) {
                        setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: 600;"); // Medium load - amber
                    } else {
                        setStyle("-fx-text-fill: #10B981; -fx-font-weight: 600;"); // Low load - green
                    }
                }
            }
        });
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", "Operation Failed", message);
    }

    private void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Success", "Operation Completed", message);
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