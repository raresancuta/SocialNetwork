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

public class CreateAccountController {
    private Service service;
    private Stage dialogStage;

    @FXML
    private TextField textFieldFirstName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;

    public void setService(Service service, Stage dialogStage ) {
        this.service = service;
        this.dialogStage = dialogStage;
    }

    public Long generateID() {
        Iterable<User> users = service.getUsers();
        Long max_id = -1L;
        for (User user : users) {
            if (user.getId() > max_id) {
                max_id = user.getId();
            }
        }
        return max_id + 1;
    }

    public void handleCreateAccount() {
        Long id = generateID();
        String first_name = textFieldFirstName.getText();
        String last_name = textFieldLastName.getText();
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();
        try{
            User user = new User(first_name,last_name,email);
            user.setId(id);
            service.addUser(user);
            LoginCredentials loginCredentials = new LoginCredentials(id,email, password);
            service.saveLoginCredetials(loginCredentials);
            dialogStage.close();
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"CONFIRMARTION","Account created successfully");

        }
        catch(ValidationException | RepositoryException e){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"ERROR",e.getMessage());
            clearFields();
        }
    }

    public void clearFields(){
        textFieldFirstName.setText("");
        textFieldLastName.setText("");
        textFieldEmail.setText("");
        textFieldPassword.setText("");
    }

    public void handleCancel(){
        dialogStage.close();
    }

}
