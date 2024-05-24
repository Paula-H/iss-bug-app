package org.example.controller.additional;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.Bug;

public class BugController {
    private Bug bug;
    @FXML
    public Label nameLabel;
    @FXML
    public Label descriptionLabel;
    public void setBug(Bug bug){
        this.bug = bug;
        this.nameLabel.setText(bug.getName());
        this.descriptionLabel.setText(bug.getDescription());
    }
}
