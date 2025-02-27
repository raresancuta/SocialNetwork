package ubb.scs.map.socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ubb.scs.map.socialnetwork.domain.LoginCredentials;
import ubb.scs.map.socialnetwork.domain.User;
import ubb.scs.map.socialnetwork.domain.validators.ValidationException;
import ubb.scs.map.socialnetwork.repository.RepositoryException;
import ubb.scs.map.socialnetwork.service.Service;

public class UpdateUserDataController {
    private User user;
    private Stage dialogStage;
    private Service service;

    @FXML
    TextField textFieldFirstName;
    @FXML
    TextField textFieldLastName;
    @FXML
    TextField textFieldEmail;
    @FXML
    TextField textFieldPassword;

    public void setService(Service service,User user, Stage dialogStage) {
        this.service = service;
        this.user = user;
        this.dialogStage = dialogStage;
    }

    public void handleUpdateData() {
        String firstName = textFieldFirstName.getText();
        String lastName = textFieldLastName.getText();
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();
        try{
            User update_user = new User(firstName.equals("")? user.getFirstName() : firstName, lastName.equals("")? user.getLastName() : lastName, email.equals("")? user.getEmail() : email);
            update_user.setId(user.getId());
            service.updateUser(update_user);
            if(!password.equals("")) {
                LoginCredentials loginCredentials = new LoginCredentials(user.getId(),email.equals("")? user.getEmail() : email,password);
                service.updateLoginCredentials(loginCredentials);
            }
            dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Confirmation","Data succesfuly modified");
        }
        catch (RepositoryException | ValidationException e){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error",e.getMessage());
        }
    }

    public void handleCancel() {
        dialogStage.close();
    }
}
