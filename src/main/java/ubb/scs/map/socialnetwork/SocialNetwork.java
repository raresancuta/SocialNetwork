package ubb.scs.map.socialnetwork;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ubb.scs.map.socialnetwork.controller.LoginController;
import ubb.scs.map.socialnetwork.domain.Friendship;
import ubb.scs.map.socialnetwork.domain.Network;
import ubb.scs.map.socialnetwork.domain.Tuple;
import ubb.scs.map.socialnetwork.domain.User;
import ubb.scs.map.socialnetwork.domain.validators.UserValidator;
import ubb.scs.map.socialnetwork.repository.FriendshipRepository;
import ubb.scs.map.socialnetwork.repository.Repository;
import ubb.scs.map.socialnetwork.repository.database.*;
import ubb.scs.map.socialnetwork.service.Service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class SocialNetwork extends javafx.application.Application {
    private Service service;
    @Override
    public void start(Stage stage) throws IOException {
        String username = "postgres";
        String password = "postgres";
        String url = "jdbc:postgresql://localhost:5432/SocialNetwork";
        Repository<Long, User> repoUsers = new UserDBRepository(url, username, password);
        FriendshipRepository repoFriendships = new FriendshipDBRepository(url, username, password);
        Network network = new Network(repoUsers, repoFriendships);
        LoginCredentialsDbRepository repoLoginCredentials = new LoginCredentialsDbRepository(url, username, password);
        FriendRequestsDbRepository repoFriendRequests = new FriendRequestsDbRepository(url, username, password);
        MessagesDbRepository repoMessages =null;
        try {
            repoMessages = new MessagesDbRepository(url, username, password);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        service = new Service(repoUsers,network,new UserValidator(),repoLoginCredentials,repoFriendRequests,repoMessages);
        initLoginView(stage);

    }

    public void initLoginView(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ubb/scs/map/socialnetwork/login-view.fxml"));
        AnchorPane loginView = (AnchorPane) fxmlLoader.load();
        Scene scene = new Scene(loginView);
        stage.setScene(scene);
        stage.setTitle("Social Network");
        stage.show();
        LoginController loginController =fxmlLoader.getController();
        loginController.setServ(service,stage);
    }

    public static void main(String[] args) {
        launch();
    }
}