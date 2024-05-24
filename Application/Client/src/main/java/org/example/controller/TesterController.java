package org.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.*;
import org.example.controller.additional.AddBugController;
import org.example.controller.additional.AddController;
import org.example.controller.additional.ModifyBugController;
import org.example.controller.additional.PasswordController;
import org.example.observer.Event;
import org.example.observer.EventType;
import org.example.observer.Observer;

import java.io.IOException;
import java.util.Optional;

public class TesterController implements Observer{
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
    private Tester loggedTester;
    private Service service;
    private ObservableList<BugDTO> bugs = FXCollections.observableArrayList();
    private BugDTO selectedBug = null;
    @FXML
    public ListView bugsListView;
    @FXML
    public Label testerName;
    @FXML
    public ComboBox sortBugsComboBox;


    void setProps(Tester tester, Service service) {
        this.loggedTester = tester;
        this.service = service;
        this.testerName.setText("Tester "+tester.getName()+" "+tester.getSurname());
        initializeBugList();
        initializeBugListListener();
        initializeComboBox();
    }

    public void handleAddBug() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/add-bug-view.fxml"));
        Parent addBug = loader.load();
        addBug.setUserData(this);
        AddBugController addController = loader.getController();
        addController.setProps(service, loggedTester);
        Scene newScene = new Scene(addBug);
        Stage newStage = new Stage(); // Create a new stage
        newStage.setScene(newScene);
        newStage.show();
    }

    public void handleModifyBug() throws IOException {
        if(selectedBug == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No bug selected!");
            alert.showAndWait();
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modify-bug-view.fxml"));
        Parent modifyBug = loader.load();
        modifyBug.setUserData(this);
        ModifyBugController addController = loader.getController();
        addController.setProps(service, selectedBug.getBug());
        Scene newScene = new Scene(modifyBug);
        Stage newStage = new Stage();
        newStage.setScene(newScene);
        newStage.show();
    }

    public void handleChangePassword() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/password-view.fxml"));
        Parent changePassword = loader.load();
        changePassword.setUserData(this);
        PasswordController passwordController = loader.getController();
        passwordController.setProps(service, new EmployeeDTO(loggedTester, EmployeeType.Tester));
        Scene newScene = new Scene(changePassword);
        Stage newStage = new Stage();
        newStage.setScene(newScene);
        newStage.show();
    }

    public void handleLogOut() throws IOException {
        this.service.logOut(new EmployeeDTO(loggedTester, EmployeeType.Tester));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
        Parent loginViewParent = loader.load();
        LoginController loginController = loader.getController();
        loginController.setServer(service);

        Stage stage = (Stage) testerName.getScene().getWindow(); // Get the current stage
        Scene loginScene = new Scene(loginViewParent);
        stage.setScene(loginScene);
        stage.setTitle("Login");
        stage.show();

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

    @Override
    public void update(Event event) {
        if(event.getType() == EventType.refresh_bugs){
            Platform.runLater(() -> {
                bugs.clear();
                bugsListView.getItems().clear();
                event.getBugs().forEach(bug -> {
                    bugs.add(new BugDTO(bug,bug.getStatus()));
                });
                bugsListView.setItems(bugs);});
        };

    }
}
