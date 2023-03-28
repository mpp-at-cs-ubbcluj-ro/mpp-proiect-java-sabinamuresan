package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.StartApplicationFX;
import org.example.model.Volunteer;
import org.example.service.Service;

import java.io.IOException;

public class LogInController {
    private AnchorPane root;
    private Stage stage;
    private Scene scene;
    private Service service;
    @FXML
    public PasswordField loginPasswordField;
    @FXML
    public TextField loginUsernameField;

    public void setService(Service service){
        this.service = service;
    }

    public void switchToHomePage(ActionEvent event) throws IOException {
        String username = loginUsernameField.getText();
        if(username.isEmpty()){
            MessageAlert.showErrorMessage(null, "Please enter the username!");
            return;
        }

        String password = loginPasswordField.getText();
        if(password.isEmpty()){
            MessageAlert.showErrorMessage(null, "Please enter the password!");
            return;
        }

        Volunteer user = service.findVolunteerAccount(username, password);
        if(user==null){
            MessageAlert.showErrorMessage(null, "This user doesn't exist!");
            return;
        }

        FXMLLoader loader = new FXMLLoader(StartApplicationFX.class.getResource("/view/HomeView.fxml"));
        root = loader.load();

        HomeController homeController = loader.getController();
        homeController.setService(service);
        homeController.setOwnerUser(user);
        homeController.initWelcome();
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
