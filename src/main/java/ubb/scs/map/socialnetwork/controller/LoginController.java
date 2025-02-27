package ubb.scs.map.socialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ubb.scs.map.socialnetwork.domain.LoginCredentials;
import ubb.scs.map.socialnetwork.domain.User;
import ubb.scs.map.socialnetwork.service.Service;


import java.io.IOException;
import java.util.Optional;

public class LoginController {
    private Service serv;
    private Stage primaryStage;

    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;

    public void setServ(Service serv,Stage primaryStage) {
        this.serv = serv;
        this.primaryStage = primaryStage;
    }

    public void handleLogin() throws IOException {
        String email = textFieldEmail.getText();
        String password = textFieldPassword.getText();
        Optional<User> userOptional = serv.verifyLoginCredentials(email, password);
        if(userOptional.isEmpty()){
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Log In","Incorrect credentials");
            clearFields();
        }
        else{
            User user = userOptional.get();
            primaryStage.close();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(LoginController.class.getResource("/ubb/scs/map/socialnetwork/userPage-view.fxml"));
                AnchorPane userView = (AnchorPane) fxmlLoader.load();
                UserPageController controller = fxmlLoader.getController();
                controller.setService(serv, user, primaryStage);
                primaryStage.setScene(new Scene(userView));
                primaryStage.setTitle("Social Network");
                //primaryStage.setWidth(800);
                primaryStage.show();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    public void handleCreateAccount() throws IOException {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ubb/scs/map/socialnetwork/createAccount-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create Account");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            CreateAccountController controller = loader.getController();
            controller.setService(serv, dialogStage);

            dialogStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void clearFields(){
        textFieldEmail.setText("");
        textFieldPassword.setText("");
    }

}
