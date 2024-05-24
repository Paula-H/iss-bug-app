package org.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.example.*;
import org.example.controller.additional.BugController;
import org.example.controller.additional.BugHistoryController;
import org.example.controller.additional.PasswordController;
import org.example.observer.Event;
import org.example.observer.EventType;
import org.example.observer.Observer;

import java.io.IOException;

public class DeveloperController implements Observer {
    private class BugDTO{
        private Bug bug;
        private BugStatus status;

        public BugDTO(Bug bug, BugStatus status){
            this.bug = bug;
            this.status = status;
        }

        public Bug getBug(){
            return bug;
        }

        public BugStatus getStatus(){
            return status;
        }

        @Override
        public String toString() {
            if (status.equals(BugStatus.Unfixed)){
                return bug.getName();
            }
            else{
                return "SOLVED "+bug.getName();
            }
        }
    }
    private Developer loggedDeveloper;
    private Service service;
    private ObservableList<BugDTO> bugs = FXCollections.observableArrayList();
    private BugDTO selectedBug = null;
    @FXML
    public Label developerName;
    @FXML
    public ListView bugsListView;
    @FXML
    public ComboBox<String> sortBugsComboBox;


    void setProps(Developer developer, Service service) {
        this.loggedDeveloper = developer;
        this.service = service;
        this.developerName.setText("Developer "+developer.getName()+" "+developer.getSurname());
        initializeBugList();
        initializeBugListListener();
        initializeBugListDoubleClick();
        initializeComboBox();
    }

    private void initializeComboBox(){
        this.sortBugsComboBox.setItems(FXCollections.observableArrayList("name ASC","name DESC"));
        this.sortBugsComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if(newValue.equals("name ASC")){
                bugs.sort((o1, o2) -> o1.getBug().getName().compareTo(o2.getBug().getName()));
            }
            else if(newValue.equals("name DESC")){
                bugs.sort((o1, o2) -> o2.getBug().getName().compareTo(o1.getBug().getName()));
            }

        });
    }

    private void initializeBugList(){
        bugs.clear();
        bugsListView.getItems().clear();
        this.service.getUnfixedBugs().forEach(bug -> {
            bugs.add(new BugDTO(bug,bug.getStatus()));
        });
        bugsListView.setItems(bugs);
    }

    private void initializeBugListListener(){
        bugsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.selectedBug = (BugDTO) newValue;
        });
    }

    private void initializeBugListDoubleClick() {
        bugsListView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                try {
                    handleSeeBugPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void handleSolveBug(){
        if(selectedBug == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No bug selected");
            alert.setContentText("Please select a bug to solve!");
            alert.showAndWait();
            return;
        }
        this.service.solveBug(selectedBug.getBug(),loggedDeveloper);
        //initializeBugList();
    }

    public void handleSeeBugPreview() throws IOException {
        if(selectedBug == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No bug selected");
            alert.setContentText("Please select a bug to see the preview!");
            alert.showAndWait();
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/bug-view.fxml"));
        Parent changePassword = loader.load();
        BugController bugController = loader.getController();
        bugController.setBug(selectedBug.bug);
        Scene newScene = new Scene(changePassword);
        Stage newStage = new Stage();
        newStage.setScene(newScene);
        newStage.show();
    }

    public void handleChangePassword() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/password-view.fxml"));
        Parent changePassword = loader.load();
        changePassword.setUserData(this);
        PasswordController passwordController = loader.getController();
        passwordController.setProps(service, new EmployeeDTO(loggedDeveloper, EmployeeType.Developer));
        Scene newScene = new Scene(changePassword);
        Stage newStage = new Stage();
        newStage.setScene(newScene);
        newStage.show();
    }

    public void handleLogOut() throws IOException {
        this.service.logOut(new EmployeeDTO(loggedDeveloper, EmployeeType.Developer));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
        Parent loginViewParent = loader.load();
        LoginController loginController = loader.getController();
        loginController.setServer(service);

        Stage stage = (Stage) developerName.getScene().getWindow(); // Get the current stage
        Scene loginScene = new Scene(loginViewParent);
        stage.setScene(loginScene);
        stage.setTitle("Login");
        stage.show();

    }


    @Override
    public void update(Event event) {
        if(event.getType() == EventType.refresh_bugs){
            Platform.runLater(() -> {
                bugs.clear();
                bugsListView.getItems().clear();
                event.getBugs().forEach(bug -> {
                    bugs.add(new BugDTO(bug,bug.getStatus()));
                });
                bugsListView.setItems(bugs);
                sortBugsComboBox.getSelectionModel().clearSelection();
            });
        }

    }
}
