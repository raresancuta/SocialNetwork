package ubb.scs.map.socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ubb.scs.map.socialnetwork.SocialNetwork;
import ubb.scs.map.socialnetwork.domain.Friend;
import ubb.scs.map.socialnetwork.domain.User;
import ubb.scs.map.socialnetwork.repository.RepositoryException;
import ubb.scs.map.socialnetwork.service.Service;
import ubb.scs.map.socialnetwork.utils.events.FriendshipEntityChangeEvent;
import ubb.scs.map.socialnetwork.utils.events.UserEntityChangeEvent;
import ubb.scs.map.socialnetwork.utils.observer.Observer;
import ubb.scs.map.socialnetwork.utils.paging.Page;
import ubb.scs.map.socialnetwork.utils.paging.Pageable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static javafx.application.Platform.exit;

public class UserPageController implements Observer<UserEntityChangeEvent> {
    private User user;
    private Stage primaryStage;
    Service service;
    ObservableList<Friend> model = FXCollections.observableArrayList();

    @FXML
    TableView<Friend> tableView;
    @FXML
    TableColumn<Friend,String> tableColumnFirstName;
    @FXML
    TableColumn<Friend,String> tableColumnLastName;
    @FXML
    TableColumn<Friend,String> tableColumnEmail;
    @FXML
    TableColumn<Friend, LocalDateTime> tableColumnFriendsFrom;
    @FXML
    Label labelUserName;
    @FXML
    Label labelPage;
    @FXML
    private Button buttonNext;
    @FXML
    private Button buttonPrevious;

    private int pageSize = 5;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    public void setService(Service service, User user, Stage primaryStage) {
        this.service = service;
        this.primaryStage = primaryStage;
        this.user = user;
        service.addObserver(this);
        initLabels();
        initModel();
        handleNewFriendshipRequestsAlert();
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<Friend,String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<Friend,String>("lastName"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<Friend,String>("email"));
        tableColumnFriendsFrom.setCellValueFactory(new PropertyValueFactory<Friend,LocalDateTime>("friendsFrom"));
        tableView.setItems(model);
    }

    public void initModel(){
        //Iterable<Friend> users = service.getUserFriends(user);
        //List<Friend> userList = StreamSupport.stream(users.spliterator(),false).collect(Collectors.toList());
        //model.setAll(userList);

        Page<Friend> page = service.findAllOnPage(new Pageable(currentPage, pageSize), user);
        int maxPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / pageSize) - 1;
        if (maxPage == -1) {
            maxPage = 0;
        }
        if (currentPage > maxPage) {
            currentPage = maxPage;
            page = service.findAllOnPage(new Pageable(currentPage, pageSize), user);
        }
        totalNumberOfElements = page.getTotalNumberOfElements();
        buttonPrevious.setDisable(currentPage == 0);
        buttonNext.setDisable((currentPage + 1) * pageSize >= totalNumberOfElements);
        List<Friend> friendsList = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(friendsList);
        labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
    }

    public void initLabels(){
        labelUserName.setText(user.getFirstName()+" "+user.getLastName());
    }

    public void update(UserEntityChangeEvent userEntityChangeEvent) {
        try{
            Optional<User> userOptional = service.findUser(user);
            if(userOptional.isPresent()){
                user = userOptional.get();
            }
        }
        catch(RepositoryException e){
            handleLogOut();
        }
        initModel();
        initLabels();
        handleNewFriendshipRequestsAlert();
    }

    public void handleNewFriendshipRequestsAlert(){
        if(service.verifyNewFriendRequests(user)){
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/ubb/scs/map/socialnetwork/newFriendRequestsAlert-view.fxml"));

                AnchorPane root = (AnchorPane) loader.load();
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Add Friend");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                NewFriendRequestsAlertController controller = loader.getController();
                controller.setService(service,user,dialogStage);

                dialogStage.show();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    public void handleDeleteFriendship() {
        Friend friend = tableView.getSelectionModel().getSelectedItem();
        if (friend != null) {
            try {
                service.removeFriendship(user.getId(), friend.getId());
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Confirmation","Friend deleted successfully");
            } catch (RepositoryException e) {
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
    }

    public void handleAddFriendship() {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ubb/scs/map/socialnetwork/addFriend-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add Friend");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AddFriendController controller = loader.getController();
            controller.setService(service,user,dialogStage);

            dialogStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleFriendRequests(){
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

    public void handleDeleteAccount(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ubb/scs/map/socialnetwork/deleteAccount-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Delete Account");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            DeleteAccountController controller = loader.getController();
            controller.setService(service,user,dialogStage);

            dialogStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleUpdateData(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ubb/scs/map/socialnetwork/updateUserData-view.fxml"));

            AnchorPane root = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Update Info");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            UpdateUserDataController controller = loader.getController();
            controller.setService(service,user,dialogStage);

            dialogStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleChat(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ubb/scs/map/socialnetwork/chat-view.fxml"));

            VBox root = (VBox) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chat");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            ChatController controller = loader.getController();
            controller.setService(service,user,dialogStage);

            dialogStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleLogOut(){
        primaryStage.close();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ubb/scs/map/socialnetwork/login-view.fxml"));
            AnchorPane loginView = (AnchorPane) fxmlLoader.load();
            Scene scene = new Scene(loginView);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Social Network");
            primaryStage.show();
            LoginController loginController = fxmlLoader.getController();
            loginController.setServ(service, primaryStage);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void handleNextPage(ActionEvent actionEvent) {
        currentPage ++;
        initModel();
    }

    public void handlePreviousPage(ActionEvent actionEvent) {
        currentPage --;
        initModel();
    }
}
