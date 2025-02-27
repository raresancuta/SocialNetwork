package ubb.scs.map.socialnetwork.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ubb.scs.map.socialnetwork.domain.User;
import ubb.scs.map.socialnetwork.service.Service;

import java.io.IOException;

public class NewFriendRequestsAlertController {
    private User user;
    private Service service;
    private Stage stage;
    public void setService(Service service,User user,Stage stage) {
        this.service = service;
        this.user = user;
        this.stage = stage;
    }

    public void handleRedirectFriendRequests(){
        stage.close();
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ubb/scs/map/socialnetwork/friendRequests-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Friend");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            FriendRequestsController controller = loader.getController();
            controller.setService(service,user,dialogStage);

            dialogStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleCancel(){
        stage.close();
    }
}
