package ubb.scs.map.socialnetwork.controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MessageAlert {
    public static void showMessage(Stage owner, Alert.AlertType alertType,String header,String content) {
        Alert alert = new Alert(alertType);
        alert.initOwner(owner);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showErrorMessage(Stage owner, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(owner);
        alert.setHeaderText("ERROR");
        alert.setContentText(text);
        alert.showAndWait();
    }
}
