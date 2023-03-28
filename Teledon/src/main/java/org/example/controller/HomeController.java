package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.StartApplicationFX;
import org.example.model.Case;
import org.example.model.Volunteer;
import org.example.service.Service;

import java.io.IOException;
import java.util.List;

public class HomeController {
    private Stage stage;

    private Scene scene;
    private Service service;
    private Volunteer ownerUser;

    private List<Case> cases;

    @FXML
    public Label usernameLabel;

    @FXML
    public ListView casesListView;

    @FXML
    public Button newDonationButton;
    public void setService(Service service) {
        this.service = service;
    }

    public void setOwnerUser(Volunteer user) {
        ownerUser = user;
        usernameLabel.setText(user.getUsername());
    }

    public void initWelcome() {
        newDonationButton.setVisible(false);
        cases = (List<Case>) service.getAllCases();
        ObservableList<String> items = FXCollections.observableArrayList();
        if(!cases.isEmpty()){
            cases.forEach(x ->{
                items.add(x.getId() + ". " + x.getCaseName() + " with total sum of: " + x.getSum() + "RON");
            });
        }
        casesListView.setItems(items);

    }

    public void handleSelection(MouseEvent event){
        newDonationButton.setVisible(true);
    }

    public void switchToDonation(ActionEvent event) throws IOException {
        String string = casesListView.getSelectionModel().getSelectedItem().toString();
        String[] splited = string.split(". ");
        int id = Integer.parseInt(splited[0]);
        Case myCase = service.findOneCase(id);


        FXMLLoader loader = new FXMLLoader(StartApplicationFX.class.getResource("/view/DonationView.fxml"));
        AnchorPane root = loader.load();

        DonationController donationController = loader.getController();
        donationController.setService(service);
        donationController.setOwnerUser(ownerUser);
        donationController.setCase(myCase);
        donationController.initList();

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToLoginPage(MouseEvent event)throws IOException {
        FXMLLoader loader = new FXMLLoader(StartApplicationFX.class.getResource("/view/LoginView.fxml"));
        AnchorPane root = loader.load();

        LogInController loginController = loader.getController();
        loginController.setService(service);
        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
