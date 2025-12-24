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

/**
 * Контроллер для окна Reports
 * Управляет отображением аналитических отчетов
 */
public class ReportsController {

    // Сервисы
    private final ReportService reportService = new ReportService();
    private final EmployeeService employeeService = new EmployeeService();
    private final ServiceService serviceItemService = new ServiceService();

    // Фильтры периода
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private Label totalRevenueLabel;
    @FXML private Label statusLabel;
    @FXML private TabPane reportsTabPane;

    // === Вкладка: Revenue ===
    @FXML private TableView<RevenueReportRow> revenueTable;
    @FXML private TableColumn<RevenueReportRow, LocalDate> revDateCol;
    @FXML private TableColumn<RevenueReportRow, String> revEmployeeCol;
    @FXML private TableColumn<RevenueReportRow, String> revServiceCol;
    @FXML private TableColumn<RevenueReportRow, Integer> revCountCol;
    @FXML private TableColumn<RevenueReportRow, Double> revTotalCol;
    @FXML private ComboBox<Employee> employeeFilterBox;
    @FXML private ComboBox<Service> serviceFilterBox;

    // === Вкладка: Employee Load ===
    @FXML private TableView<EmployeeLoadReportRow> employeeLoadTable;
    @FXML private TableColumn<EmployeeLoadReportRow, String> empNameCol;
    @FXML private TableColumn<EmployeeLoadReportRow, Integer> empAppointmentsCol;
    @FXML private TableColumn<EmployeeLoadReportRow, String> empWorkTimeCol;
    @FXML private TableColumn<EmployeeLoadReportRow, Double> empRevenueCol;
    @FXML private TableColumn<EmployeeLoadReportRow, Double> empLoadCol;

    // === Вкладка: Client Activity ===
    @FXML private TableView<ClientActivityReportRow> clientActivityTable;
    @FXML private TableColumn<ClientActivityReportRow, String> clientNameCol;
    @FXML private TableColumn<ClientActivityReportRow, String> clientPhoneCol;
    @FXML private TableColumn<ClientActivityReportRow, Integer> clientVisitsCol;
    @FXML private TableColumn<ClientActivityReportRow, Double> clientSpentCol;
    @FXML private TableColumn<ClientActivityReportRow, String> clientLastVisitCol;

    // === Вкладка: Services ===
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

    /**
     * Настройка всех таблиц
     */
    private void setupTables() {
        // Revenue Table
        revDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        revEmployeeCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        revServiceCol.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        revCountCol.setCellValueFactory(new PropertyValueFactory<>("completedCount"));
        revTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        formatMoneyColumn(revTotalCol);

        // Employee Load Table
        empNameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        empAppointmentsCol.setCellValueFactory(new PropertyValueFactory<>("totalAppointments"));
        empWorkTimeCol.setCellValueFactory(new PropertyValueFactory<>("formattedWorkTime"));
        empRevenueCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        empLoadCol.setCellValueFactory(new PropertyValueFactory<>("loadPercentage"));
        formatMoneyColumn(empRevenueCol);
        formatPercentColumn(empLoadCol);

        // Client Activity Table
        clientNameCol.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        clientPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        clientVisitsCol.setCellValueFactory(new PropertyValueFactory<>("totalVisits"));
        clientSpentCol.setCellValueFactory(new PropertyValueFactory<>("totalSpent"));
        clientLastVisitCol.setCellValueFactory(new PropertyValueFactory<>("lastVisitDate"));
        formatMoneyColumn(clientSpentCol);

        // Services Table
        svcNameCol.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        svcBookedCol.setCellValueFactory(new PropertyValueFactory<>("timesBooked"));
        svcRevenueCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        svcAvgPriceCol.setCellValueFactory(new PropertyValueFactory<>("averagePrice"));
        formatMoneyColumn(svcRevenueCol);
        formatMoneyColumn(svcAvgPriceCol);
    }

    /**
     * Загрузка данных для фильтров
     */
    private void loadFilters() {
        // Загрузка сотрудников
        List<Employee> employees = employeeService.getAllEmployees();
        ObservableList<Employee> employeeList = FXCollections.observableArrayList(employees);
        employeeFilterBox.setItems(employeeList);
        employeeFilterBox.setPromptText("All employees");

        // Загрузка услуг
        List<Service> services = serviceItemService.getAllServices();
        ObservableList<Service> serviceList = FXCollections.observableArrayList(services);
        serviceFilterBox.setItems(serviceList);
        serviceFilterBox.setPromptText("All services");
    }

    /**
     * Установка дат по умолчанию (текущий месяц)
     */
    private void setDefaultDates() {
        LocalDate now = LocalDate.now();
        fromDatePicker.setValue(now.withDayOfMonth(1));
        toDatePicker.setValue(now);
    }

    /**
     * Генерация всех отчетов
     */
    @FXML
    private void onGenerate() {
        try {
            LocalDate from = fromDatePicker.getValue();
            LocalDate to = toDatePicker.getValue();

            if (from == null || to == null) {
                showError("Please select both start and end dates");
                return;
            }

            List<RevenueReportRow> revenueData = reportService.generateRevenueReport(from, to, null, null);
            System.out.println("Revenue rows: " + revenueData.size());

            if (!revenueData.isEmpty()) {
                System.out.println("First row: " + revenueData.get(0));
            }

            // Генерация отчетов в зависимости от активной вкладки
            generateAllReports(from, to);

            // Обновление общей выручки
            Double totalRevenue = reportService.getTotalRevenue(from, to);
            totalRevenueLabel.setText(String.format("%.2f ₸", totalRevenue));

            updateStatus("Reports generated successfully for period: " + from + " to " + to);

        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error generating reports: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Генерация всех отчетов сразу
     */
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

    /**
     * Применение фильтров для отчета по выручке
     */
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

            updateStatus("Revenue report filtered");

        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Error applying filters: " + e.getMessage());
        }
    }

    /**
     * Экспорт текущего отчета в CSV
     */
    @FXML
    private void onExport() {
        try {
            Tab selectedTab = reportsTabPane.getSelectionModel().getSelectedItem();
            String tabName = selectedTab.getText();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Report");
            fileChooser.setInitialFileName("report_" + tabName.toLowerCase().replace(" ", "_") + ".csv");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );

            File file = fileChooser.showSaveDialog(reportsTabPane.getScene().getWindow());
            if (file != null) {
                exportCurrentTab(tabName, file.getAbsolutePath());
                showInfo("Report exported successfully to: " + file.getName());
            }

        } catch (Exception e) {
            showError("Error exporting report: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Экспорт текущей вкладки
     */
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

    /**
     * Сброс фильтров
     */
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

    /**
     * Форматирование колонки с деньгами
     */
    private <T> void formatMoneyColumn(TableColumn<T, Double> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f ₸", amount));
                }
            }
        });
    }

    /**
     * Форматирование колонки с процентами
     */
    private <T> void formatPercentColumn(TableColumn<T, Double> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double percent, boolean empty) {
                super.updateItem(percent, empty);
                if (empty || percent == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f%%", percent));
                }
            }
        });
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}