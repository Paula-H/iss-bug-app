package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.*;
import org.example.observer.Event;
import org.example.observer.Observer;

import java.io.IOException;

public class LoginController{
    @FXML
    public TextField usernameField;

    @FXML
    public TextField passwordField;

    public Service server;

    public void setServer(Service server){
        this.server = server;
    }

    public void handleLogIn() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if(username.equals("") || password.equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please complete all fields before trying to log in!");
            alert.showAndWait();
            this.usernameField.clear();
            this.passwordField.clear();
            return;
        }

        FXMLLoader loader;

        EmployeeDTO login = null;

        try{
        login = this.server.logIn(username, password,null);
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Invalid username or password!");
            alert.showAndWait();
            this.usernameField.clear();
            this.passwordField.clear();
            return;
        }



        AdminController adminController = null;
        DeveloperController developerController = null;
        TesterController testerController = null;

        switch (login.type){
            case Admin:
                loader = new FXMLLoader(getClass().getResource("/admin-view.fxml"));
                Parent adminView = loader.load();
                adminController = loader.getController();
                var admin = this.server.logIn(username, password,adminController);
                if(!admin.employee.getActive()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Account is not active!");
                    alert.showAndWait();
                    this.usernameField.clear();
                    this.passwordField.clear();
                    return;
                }
                adminController.setProps((Admin) admin.employee,server);
                showNewWindow(adminView);
                break;
            case Developer:
                loader = new FXMLLoader(getClass().getResource("/developer-view.fxml"));
                Parent developerView = loader.load();
                developerController = loader.getController();
                var developer = this.server.logIn(username, password,developerController);
                if(!developer.employee.getActive()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Account is not active!");
                    alert.showAndWait();
                    this.usernameField.clear();
                    this.passwordField.clear();
                    return;
                }
                developerController.setProps((Developer) developer.employee,server);
                showNewWindow(developerView);
                break;
            case Tester:
                loader = new FXMLLoader(getClass().getResource("/tester-view.fxml"));
                Parent testerView = loader.load();
                testerController = loader.getController();
                var tester = this.server.logIn(username, password,testerController);
                if(!tester.employee.getActive()){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Account is not active!");
                    alert.showAndWait();
                    this.usernameField.clear();
                    this.passwordField.clear();
                    return;
                }
                testerController.setProps((Tester) tester.employee,server);
                showNewWindow(testerView);
                break;
        }
    }

    private void showNewWindow(Parent newWindow) {
        Scene volunteerViewScene = new Scene(newWindow);
        Stage stage = (Stage) usernameField.getScene().getWindow(); // Get the current stage
        stage.setScene(volunteerViewScene);
        stage.show();
    }


}
