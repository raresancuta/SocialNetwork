package ubb.scs.map.socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import ubb.scs.map.socialnetwork.domain.Friend;
import ubb.scs.map.socialnetwork.domain.Message;
import ubb.scs.map.socialnetwork.domain.User;
import ubb.scs.map.socialnetwork.service.Service;

import javafx.scene.control.*;
import ubb.scs.map.socialnetwork.utils.events.UserEntityChangeEvent;
import ubb.scs.map.socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ChatController implements Observer<UserEntityChangeEvent> {
    private User user;
    private Stage stage;
    private Service service;
    @FXML
    private TextField receiverField;

    @FXML
    private ListView<Message> messageListView;
    @FXML
    private ComboBox<Friend> userComboBox;
    @FXML
    private TextArea messageField;

    private ObservableList<Message> messagesObservableList = FXCollections.observableArrayList();
    private ObservableList<Friend> usersObservableList = FXCollections.observableArrayList();

    public void setService(Service service, User user, Stage stage) {
        this.service = service;
        this.user = user;
        this.stage = stage;
        service.addObserver(this);
        initMessages();
    }

    @FXML
    public void initialize() {
        messageListView.setItems(messagesObservableList);
        messageListView.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            @Override
            public ListCell<Message> call(ListView<Message> listView) {
                return new ListCell<Message>() {
                    @Override
                    protected void updateItem(Message message, boolean empty) {
                        super.updateItem(message, empty);
                        if (empty || message == null) {
                            setText(null);
                        } else {
                            setText(String.format("[%s] %s %s to %s %s: %s",
                                    message.getDate(),
                                    message.getSender().getFirstName(),
                                    message.getSender().getLastName(),
                                    message.getReceivers().get(0).getFirstName(),
                                    message.getReceivers().get(0).getLastName(),
                                    message.getContent()));
                        }
                    }
                };
            }
        });
        userComboBox.setCellFactory(new Callback<ListView<Friend>, ListCell<Friend>>() {
            @Override
            public ListCell<Friend> call(ListView<Friend> listView) {
                return new ListCell<Friend>() {
                    @Override
                    protected void updateItem(Friend friend, boolean empty) {
                        super.updateItem(friend, empty);
                        if (empty || friend == null) {
                            setText(null);
                        } else {
                            setText(String.format("%s %s",
                                    friend.getFirstName(),
                                    friend.getLastName()));
                        }
                    }
                };
            }
            });

        userComboBox.setConverter(new StringConverter<Friend>() {
            @Override
            public String toString(Friend s) {
                if (s == null) {
                    return null;
                } else {
                    return s.getFirstName() + " " + s.getLastName();
                }
            }
            @Override
            public Friend fromString(String friendString) {
                return null;
            }
        });
        userComboBox.setItems(usersObservableList);
    }

    @FXML
    private void initMessages() {
            List<Message> messages = service.findAllMessagesForUser(user);
            messagesObservableList.clear();
            messagesObservableList.addAll(messages);
            Iterable<Friend> friends = service.getUserFriends(user);
            usersObservableList.clear();
            List<Friend> firendList = StreamSupport.stream(friends.spliterator(),false).collect(Collectors.toList());
            usersObservableList.addAll(firendList);
    }

    @FXML
    private void sendMessage() {
        try {
            Friend receiver = userComboBox.getSelectionModel().getSelectedItem();
            String content = messageField.getText();

            if (receiver == null || content.isEmpty()) {
                MessageAlert.showMessage(null,Alert.AlertType.ERROR,"Error", "All fields must be completed!");
                return;
            }

            Message message = new Message(
                    user,
                    List.of(receiver),
                    content,
                    LocalDateTime.now(),
                    null
            );

            service.saveMessage(message);
            //MessageAlert.showMessage(null,Alert.AlertType.INFORMATION,"Success", "Message sent successfully!");
            messageField.clear();
        } catch (Exception e) {
            e.printStackTrace();
            MessageAlert.showMessage(null, Alert.AlertType.ERROR,"Error", "An error occurred while sending the message.");
        }
    }

    @FXML
    private void replyToMessage() {
        Message selectedMessage = messageListView.getSelectionModel().getSelectedItem();
        if (selectedMessage == null) {
            MessageAlert.showMessage(null,Alert.AlertType.ERROR,"Error", "Please select a message to reply to!");
            return;
        }

        try {
            String content = messageField.getText();

            if (content.isEmpty()) {
                MessageAlert.showMessage(null,Alert.AlertType.ERROR,"Error", "The reply message cannot be empty!");
                return;
            }
            Message replyMessage = new Message(
                    user,
                    List.of(selectedMessage.getSender()),
                    content,
                    LocalDateTime.now(),
                    selectedMessage
            );

            service.saveMessage(replyMessage);
            messageField.clear();
            //MessageAlert.showMessage(null,Alert.AlertType.INFORMATION,"Success", "Reply sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            MessageAlert.showMessage(null,Alert.AlertType.ERROR,"Error", "An error occurred while sending the reply.");
        }
    }

    public void update(UserEntityChangeEvent event) {
        initMessages();
    }

}
