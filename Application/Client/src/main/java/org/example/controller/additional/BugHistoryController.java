package org.example.controller.additional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.example.Bug;
import org.example.BugStatus;
import org.example.Service;
import org.example.controller.DeveloperController;
import org.example.observer.Event;
import org.example.observer.Observer;

public class BugHistoryController implements Observer {
    @Override
    public void update(Event event) {

    }

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
    private Service service;
    @FXML
    public ListView bugsListView;
    private ObservableList<BugDTO> bugs = FXCollections.observableArrayList();

    public void setProps(Service service){
        this.service = service;
        initializeBugList();
    }

    private void initializeBugList(){
        bugs.clear();
        bugsListView.getItems().clear();
        this.service.getBugs().forEach(bug -> {
            bugs.add(new BugDTO(bug,bug.getStatus()));
        });
        bugsListView.setItems(bugs);
    }
}
