package org.example.controller.additional;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.Bug;
import org.example.Service;

import java.util.Objects;

public class ModifyBugController {
    private Service service;
    private Bug modifiedBug;
    @FXML
    public Label status;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public TextArea description;
    @FXML
    public Label bugName;

    public void setProps(Service service, Bug bug){
        this.service = service;
        this.modifiedBug = bug;
        this.bugName.setText(bug.getName());
    }

    public void handleModifyBug(){
        if(Objects.equals(description.getText(), "")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("Could not modify bug!");
            alert.setContentText("Description cannot be empty!");
            alert.showAndWait();
            return;
        }
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                service.modifyBug(modifiedBug, description.getText());
                Thread.sleep(10000);
                return null;
            }
        };

        this.progressBar.setVisible(true);
        this.status.setVisible(true);

        task.setOnSucceeded(e -> {
            this.status.setVisible(false);
            this.progressBar.setVisible(false);
            ((Stage) this.status.getScene().getWindow()).close();
        });

        new Thread(task).start();
    }
}
