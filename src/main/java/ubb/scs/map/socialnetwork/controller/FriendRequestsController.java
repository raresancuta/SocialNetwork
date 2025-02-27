package ubb.scs.map.socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ubb.scs.map.socialnetwork.domain.FriendRequest;
import ubb.scs.map.socialnetwork.domain.User;
import ubb.scs.map.socialnetwork.repository.RepositoryException;
import ubb.scs.map.socialnetwork.service.Service;

import java.time.LocalDateTime;
import java.util.List;

public class FriendRequestsController {
    private User user;
    private Stage dialogStage;
    private Service service;
    ObservableList<FriendRequest> model = FXCollections.observableArrayList();
    @FXML
    TableView<FriendRequest> tableView;

    @FXML
    TableColumn<FriendRequest, String> tableColumnSenderFirstName;
    @FXML
    TableColumn<FriendRequest, String> tableColumnSenderLastName;
    @FXML
    TableColumn<FriendRequest, LocalDateTime> tableColumnFriendRequestFromDate;

    public void setService(Service service,User user, Stage dialogStage) {
        this.service = service;
        this.user = user;
        this.dialogStage = dialogStage;
        initModel();
    }

    @FXML
    public void initialize(){
        tableColumnSenderFirstName.setCellValueFactory(new PropertyValueFactory<FriendRequest,String>("firstName"));
        tableColumnSenderLastName.setCellValueFactory(new PropertyValueFactory<FriendRequest,String>("lastName"));
        tableColumnFriendRequestFromDate.setCellValueFactory(new PropertyValueFactory<FriendRequest,LocalDateTime>("requestFrom"));
        tableView.setItems(model);
    }

    public void initModel() {
        List<FriendRequest> friendRequests = service.getUserFriendRequests(user);
        model.setAll(friendRequests);
    }

    public void handleAcceptFriendship() {
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                service.deleteFriendRequest(selectedUser.getId(), user.getId());
                service.addFriendship(user.getId(), selectedUser.getId());
                dialogStage.close();
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Confirmation", "Friend added successfully");
            } catch (RepositoryException e) {
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
    }

    public void handleDeclineFriendship(){
        User selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                service.deleteFriendRequest(selectedUser.getId(),user.getId());
                dialogStage.close();
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Confirmation", "Request denied successfully");
            } catch (RepositoryException e) {
                MessageAlert.showMessage(null, Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
    }

    public void handleCancel(){
        dialogStage.close();
    }


}
