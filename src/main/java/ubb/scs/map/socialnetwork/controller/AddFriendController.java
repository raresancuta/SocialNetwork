package ubb.scs.map.socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ubb.scs.map.socialnetwork.domain.Friendship;
import ubb.scs.map.socialnetwork.domain.User;
import ubb.scs.map.socialnetwork.repository.RepositoryException;
import ubb.scs.map.socialnetwork.service.Service;
import ubb.scs.map.socialnetwork.utils.observer.Observable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddFriendController {
    User user;
    Service service;
    Stage dialogStage;
    ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User,String> tableColumnFirstName;
    @FXML
    TableColumn<User,String> tableColumnLastName;
    @FXML
    TextField textFieldUserName;

    public void setService(Service service,User user,Stage dialogStage){
        this.service = service;
        this.user = user;
        this.dialogStage = dialogStage;
        initModel();
    }

    @FXML
    public void initialize() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User,String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User,String>("lastName"));
        tableView.setItems(model);
        textFieldUserName.textProperty().addListener(o->handleUsersFilter());
    }

    public void initModel(){
        Iterable<User> users = service.getUsers();
        List<User> userList = StreamSupport.stream(users.spliterator(),false).collect(Collectors.toList());
        model.setAll(userList);
    }

    public void handleAddFriend(){
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if(user != null){
            try{
                service.saveFriendRequest(user.getId(),selectedUser.getId(),LocalDateTime.now());
                dialogStage.close();
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"Confirmation","Friendship request sent successfully");
            }
            catch(RepositoryException e){
                MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error",e.getMessage());
            }
        }
    }

    public void handleCancel(){
        dialogStage.close();
    }

    public void handleUsersFilter(){
        String targetUser = textFieldUserName.getText();
        tableView.setItems(model.filtered(user->{
            return user.toString().toLowerCase().contains(targetUser.toLowerCase()) || targetUser.isEmpty();
        }));
    }
}
