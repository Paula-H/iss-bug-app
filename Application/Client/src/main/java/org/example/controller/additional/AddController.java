package org.example.controller.additional;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Admin;
import org.example.EmployeeType;
import org.example.Service;
import org.example.controller.AdminController;

import java.io.IOException;

public class AddController {
    private Service service;

    @FXML
    public TextField nameField;
    @FXML
    public TextField surnameField;
    @FXML
    public TextField usernameField;
    @FXML
    public TextField passwordField;
    @FXML
    public DatePicker birthdatePicker;
    @FXML
    public CheckBox adminCheckBox;
    @FXML
    public CheckBox developerCheckBox;
    @FXML
    public CheckBox testerCheckBox;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Label statusUpdate;
    @FXML
    public Label status;


    public void setService(Service service) {
        this.service = service;
        this.progressBar.setVisible(false);
        this.statusUpdate.setVisible(false);
        this.status.setVisible(false);
    }

    public void handleAddEmployee() {
        if(!allFieldsCompleted()){
            clearEverything();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please complete all fields before trying to add a new employee!");
            alert.showAndWait();
            return;
        }
        if(numberBoxesChecked()==0){
            clearEverything();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please select at one role before trying to add a new employee!");
            alert.showAndWait();
            return;
        }
        if(numberBoxesChecked() != 1){
            clearEverything();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Please select only one role!");
            alert.showAndWait();
            return;
        }

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if(adminCheckBox.isSelected()){
                    service.addEmployee(EmployeeType.Admin,nameField.getText(),surnameField.getText(),birthdatePicker.getValue().atStartOfDay(),usernameField.getText(),passwordField.getText());
                }
                if(developerCheckBox.isSelected()){
                    service.addEmployee(EmployeeType.Developer,nameField.getText(),surnameField.getText(),birthdatePicker.getValue().atStartOfDay(),usernameField.getText(),passwordField.getText());
                }
                if(testerCheckBox.isSelected()){
                    service.addEmployee(EmployeeType.Tester,nameField.getText(),surnameField.getText(),birthdatePicker.getValue().atStartOfDay(),usernameField.getText(),passwordField.getText());
                }
                Thread.sleep(10000);
                return null;
            }
        };

        this.progressBar.setVisible(true);
        this.status.setVisible(true);

        task.setOnSucceeded(e -> {
            this.progressBar.setVisible(false);
            this.statusUpdate.setVisible(true);
            ((Stage) this.status.getScene().getWindow()).close();
        });

        new Thread(task).start();
    }

    private Integer numberBoxesChecked(){
        int count = 0;
        if(adminCheckBox.isSelected()){
            count++;
        }
        if(developerCheckBox.isSelected()){
            count++;
        }
        if(testerCheckBox.isSelected()){
            count++;
        }
        return count;

    }

    private Boolean allFieldsCompleted(){
        if(nameField.getText().equals("") || surnameField.getText().equals("") || usernameField.getText().equals("") || passwordField.getText().equals("") || birthdatePicker.getValue() == null){
            return false;
        }
        return true;
    }

    private void clearEverything(){
        this.nameField.setText("");
        this.surnameField.setText("");
        this.usernameField.setText("");
        this.passwordField.setText("");
        this.birthdatePicker.setValue(null);
        this.adminCheckBox.setSelected(false);
        this.developerCheckBox.setSelected(false);
        this.testerCheckBox.setSelected(false);
    }


}
