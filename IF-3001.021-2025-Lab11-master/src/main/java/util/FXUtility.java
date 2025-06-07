package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import ucr.lab.HelloApplication;

import java.io.IOException;
import java.util.Optional;

public class FXUtility {

    public static void loadPage(String className, String page, BorderPane bp) {
        try {
            Class cl = Class.forName(className);
            FXMLLoader fxmlLoader = new FXMLLoader(cl.getResource(page));
            cl.getResource("bp");
            bp.setCenter(fxmlLoader.load());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Alert alert(String title, String headerText){
        Alert myalert = new Alert(Alert.AlertType.INFORMATION);
        myalert.setTitle(title);
        myalert.setHeaderText(headerText);
        DialogPane dialogPane = myalert.getDialogPane();
        String css = HelloApplication.class.getResource("dialog.css").toExternalForm();
        dialogPane.getStylesheets().add(css);
        dialogPane.getStyleClass().add("myDialog");
        return myalert;
    }

    public static TextInputDialog dialog(String title, String headerText){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        //String css = HelloApplication.class.getResource("moderna.css").toExternalForm();
        //dialog.getEditor().getStylesheets().add(css);
        return dialog;
    }

    public static String alertYesNo(String title, String headerText, String contextText){
        Alert myalert = new Alert(Alert.AlertType.CONFIRMATION);
        myalert.setTitle(title);
        myalert.setHeaderText(headerText);
        myalert.setContentText(contextText);
        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        myalert.getDialogPane().getButtonTypes().clear(); //quita los botones defaults
        myalert.getDialogPane().getButtonTypes().add(buttonTypeYes);
        myalert.getDialogPane().getButtonTypes().add(buttonTypeNo);
        //dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        DialogPane dialogPane = myalert.getDialogPane();
        String css = HelloApplication.class.getResource("dialog.css").toExternalForm();
        dialogPane.getStylesheets().add(css);
        Optional<ButtonType> result = myalert.showAndWait();
        //if((result.isPresent())&&(result.get()== ButtonType.OK)) {
        if((result.isPresent())&&(result.get()== buttonTypeYes))
            return "YES";
        else return "NO";
    }

    public static void showTextAreaAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);

        TextArea textArea = new TextArea(contentText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        textArea.setStyle("-fx-font-weight: bold;");

        // Añadir el TextArea al DialogPane del Alert
        alert.getDialogPane().setContent(textArea);

        alert.getDialogPane().setPrefWidth(500);
        alert.getDialogPane().setPrefHeight(300);

        alert.showAndWait();
    }

    public static void showMessage(String title, String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static Optional<ButtonType> showAlert(String title, String message, Alert.AlertType information) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null); // Sin encabezado
        alert.setContentText(message);
        alert.showAndWait();
        return null;
    }


}
