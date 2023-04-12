package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.StartApplicationFX;
import org.example.model.Volunteer;
import org.example.services.IService;
import org.example.services.TeledonException;

import java.io.IOException;

public class LogInController {
    private AnchorPane root;
    private Stage stage;
    private Scene scene;
    private IService service;

    private HomeController homeController;
    @FXML
    Parent mainHomeParent;
    @FXML
    public PasswordField loginPasswordField;
    @FXML
    public TextField loginUsernameField;

    public void setService(IService service){
        this.service = service;
    }

    public void setHomeController(HomeController controller){
        this.homeController = controller;
    }

    public void setParent(Parent p) {
        this.mainHomeParent = p;
    }

    public void switchToHomePage(ActionEvent event) throws IOException, TeledonException {
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

        Volunteer user = service.login(username, password, homeController);
        if(user==null){
            MessageAlert.showErrorMessage(null, "This user doesn't exist!");
            return;
        }

        homeController.setService(service);
        homeController.setOwnerUser(user);
        homeController.initWelcome();
        homeController.initList();


        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(mainHomeParent);
        stage.setScene(scene);
        stage.show();
    }
}
