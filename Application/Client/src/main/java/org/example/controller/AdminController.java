package org.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.example.*;
import org.example.controller.additional.AddController;
import org.example.controller.additional.BugHistoryController;
import org.example.controller.additional.PasswordController;
import org.example.observer.Event;
import org.example.observer.EventType;
import org.example.observer.Observer;

import java.io.IOException;
import java.util.Optional;

public class AdminController implements Observer {
    private Admin loggedAdmin;
    private Service service;

    private ObservableList<EmployeeDTO> employees = FXCollections.observableArrayList();
    private EmployeeDTO selectedEmployee = null;

    @FXML
    public ListView employeesListView;

    @FXML
    public Label adminNameLabel;

    @FXML
    public MenuButton menuButton;
    @FXML
    public MenuItem changePassword;
    @FXML
    public MenuItem deactivateAccount;
    @FXML
    public MenuItem logOut;

    public void setProps(Admin admin, Service service) {
        this.loggedAdmin = admin;
        this.service = service;

        this.adminNameLabel.setText("Admin "+admin.getName()+" "+admin.getSurname());
        initializeListViewListener();

        initializeEmployeesList();
    }

    private void initializeListViewListener(){
        employeesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.selectedEmployee = (EmployeeDTO) newValue;
        });
    }

    private void initializeEmployeesList(){
        employees.clear();
        employeesListView.getItems().clear();
        this.service.getEmployees().forEach(employees::add);

        employeesListView.setItems(employees);

    }

    public void handleDeleteEmployee(){
        if(selectedEmployee == null){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No employee selected!");
            alert.showAndWait();
            return;
        }
        if(selectedEmployee.type == EmployeeType.Admin){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning!");
            alert.setHeaderText("An admin employee cannot be deleted!");
            alert.showAndWait();
            return;

        }
        this.service.deleteEmployee(selectedEmployee);
        initializeEmployeesList();

    }

    public Admin getLoggedAdmin() {
        return loggedAdmin;
    }

    public void handleAddEmployee() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/add-view.fxml"));
        Parent addEmployee = loader.load();
        addEmployee.setUserData(this);
        AddController addController = loader.getController();
        addController.setService(service);
        Scene newScene = new Scene(addEmployee);
        Stage newStage = new Stage(); // Create a new stage
        newStage.setScene(newScene);
        newStage.show();
    }

    public void handleChangePassword() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/password-view.fxml"));
        Parent changePassword = loader.load();
        changePassword.setUserData(this);
        PasswordController passwordController = loader.getController();
        passwordController.setProps(service, new EmployeeDTO(loggedAdmin, EmployeeType.Admin));
        Scene newScene = new Scene(changePassword);
        Stage newStage = new Stage(); // Create a new stage
        newStage.setScene(newScene);
        newStage.show();
    }

    public void handleDeactivateAccount(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deactivation Confirmation");
        alert.setHeaderText("Are you sure you want to proceed with the deactivation of your account?");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeYes){
            this.service.deactivateAccount(new EmployeeDTO(loggedAdmin, EmployeeType.Admin));
            System.exit(0);
        } else {
            alert.close();
        }
    }

    public void handleLogOut() throws IOException {
        this.service.logOut(new EmployeeDTO(loggedAdmin, EmployeeType.Admin));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
        Parent loginViewParent = loader.load();
        LoginController loginController = loader.getController();
        loginController.setServer(service);

        Stage stage = (Stage) adminNameLabel.getScene().getWindow(); // Get the current stage
        Scene loginScene = new Scene(loginViewParent);
        stage.setScene(loginScene);
        stage.setTitle("Login");
        stage.show();

    }

    public void handleViewBugHistory() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/bug-history-view.fxml"));
        Parent changePassword = loader.load();
        BugHistoryController bugHistoryController = loader.getController();
        bugHistoryController.setProps(service);
        Scene newScene = new Scene(changePassword);
        Stage newStage = new Stage();
        newStage.setScene(newScene);
        newStage.show();
    }

    @Override
    public void update(Event event) {
        if(event.getType() == EventType.refresh_employees){
            Platform.runLater(() -> {
                employees.clear();
                employeesListView.getItems().clear();
                event.getData().forEach(employee -> this.employees.add(employee));
                employeesListView.setItems(employees);
            });
        }
    }


}
