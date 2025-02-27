package ubb.scs.map.socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import ubb.scs.map.socialnetwork.domain.User;
import ubb.scs.map.socialnetwork.repository.RepositoryException;
import ubb.scs.map.socialnetwork.service.Service;

public class DeleteAccountController {
    User user;
    Stage dialogStage;
    Service service;
    @FXML
    Label labelUserName;

    public void setService(Service service,User user, Stage dialogStage) {
        this.service = service;
        this.user = user;
        this.dialogStage = dialogStage;
        labelUserName.setText(user.getFirstName()+" "+user.getLastName());
    }

    public void handleConfirmation(){
        try{
            service.removeUser(user);
            dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Confirmation","Account deleted successfully");
        } catch (RepositoryException e) {
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error",e.getMessage());
        }
    }

    public void handleCancel(){
        dialogStage.close();
    }
}
