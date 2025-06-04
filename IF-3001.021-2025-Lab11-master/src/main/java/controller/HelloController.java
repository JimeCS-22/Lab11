package controller;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import ucr.lab.HelloApplication;

import java.io.IOException;

public class HelloController {

    @FXML
    private Text txtMessage;
    @FXML
    private BorderPane bp;
    @FXML
    private AnchorPane ap;


    @Deprecated
    private void load(String form) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(form));
        this.bp.setCenter(fxmlLoader.load());
    }

    @FXML
    public void Home(ActionEvent actionEvent) {
        this.bp.setCenter(ap);
        this.txtMessage.setText("Laboratory No. 11");
    }


    @FXML
    public void Exit(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    public void exampleOnMousePressed(Event event)  {
        this.txtMessage.setText("Loading Example. Please wait!!!");
    }

    @FXML
    public void LinkedGraphOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void MatrixGraphOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void LinkedOperationsOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void ListOperationsOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void MatrixOperationsOnAction(ActionEvent actionEvent) {
    }

    @FXML
    public void ListGraphOnAction(ActionEvent actionEvent) {
    }
}


