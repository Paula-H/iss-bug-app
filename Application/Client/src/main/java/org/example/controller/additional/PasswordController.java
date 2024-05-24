package org.example.controller.additional;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.EmployeeDTO;
import org.example.Service;

public class PasswordController {
    @FXML
    public Label status;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public PasswordField oldPassword;
    @FXML
    public PasswordField newPassword;
    @FXML
    public PasswordField confirmPassword;
    private Service service;
    private EmployeeDTO employee;

    public void setProps(Service service, EmployeeDTO employee) {
        this.service = service;
        this.employee = employee;
        this.progressBar.setVisible(false);
        this.status.setVisible(false);
    }

    public void handleChangePassword(){
        if(!checkPassword()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Passwords do not match or old password is incorrect");
            alert.showAndWait();
            return;
        }
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                service.changePassword(employee, newPassword.getText());
                Thread.sleep(1000);
                return null;
            }
        };

        this.progressBar.setVisible(true);
        this.status.setVisible(true);

        task.setOnSucceeded(e -> {
            ((Stage) this.status.getScene().getWindow()).close();
        });

        new Thread(task).start();

    }

    private Boolean checkPassword(){
        return (employee.employee.getPassword().equals(oldPassword.getText()) && newPassword.getText().equals(confirmPassword.getText()));
    }

}
