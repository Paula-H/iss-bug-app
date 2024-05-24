package org.example.controller.additional;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.EmployeeType;
import org.example.Service;
import org.example.Tester;

public class AddBugController {
    private Service service;
    private Tester loggedTester;
    @FXML
    public TextField name;
    @FXML
    public TextArea description;
    @FXML
    public Label status;
    @FXML
    public ProgressBar progressBar;

    public void handleAddBug(){
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try{
                    service.addNewBug(name.getText(), description.getText(), loggedTester);
                }
                catch (RuntimeException e){
                    cancel();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Could not add bug");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    });
                    progressBar.setVisible(false);
                    status.setVisible(false);
                    name.setText("");
                    description.setText("");
                    return null;
                }

                Thread.sleep(10000);
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

    public void setProps(Service service, Tester tester){
        this.service = service;
        this.loggedTester = tester;
        this.status.setVisible(false);
        this.progressBar.setVisible(false);
    }
}
