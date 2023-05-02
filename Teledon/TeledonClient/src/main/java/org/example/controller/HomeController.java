package org.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.StartApplicationFX;
import org.example.dto.CaseDTO;
import org.example.model.Case;
import org.example.model.Donation;
import org.example.model.Donor;
import org.example.model.Volunteer;
import org.example.services.IObserver;
import org.example.services.IService;
import org.example.services.TeledonException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable, IObserver {
    private Stage stage;

    private Scene scene;
    private IService service;
    private Volunteer ownerUser;

    private List<Case> cases;

    @FXML Button addDonationButton;

    @FXML
    public Label usernameLabel;

    @FXML
    public ListView casesListView;

    private List<Donor> donors;

    private Case myCase;

    @FXML
    private TextField caseInput;

    @FXML
    private TextField nameInput;

    @FXML
    private TextField addressInput;

    @FXML
    private TextField phoneNumberInput;

    @FXML
    private TextField sumInput;

    @FXML
    private Label caseLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label sumLabel;

    @FXML
    private Label newDonationLabel;

    @FXML
    private ListView donorsListView;

    @FXML
    public TextField searchField;

    @FXML
    public AnchorPane searchBar;

    @FXML
    public Button newDonationButton;
    public void setService(IService service) {
        this.service = service;
    }

    public void setOwnerUser(Volunteer user) {
        ownerUser = user;
        usernameLabel.setText(user.getUsername());
    }

    public void initWelcome() throws TeledonException {
        //hideDonationFields();
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

    public void handleSelection(MouseEvent event) throws TeledonException {
        String string = casesListView.getSelectionModel().getSelectedItem().toString();
        String[] splited = string.split(". ");
        int id = Integer.parseInt(splited[0]);
        myCase = service.findOneCase(id);
        caseInput.setText(myCase.getCaseName());
        //newDonationButton.setVisible(true);
    }

    private void showDonationFields() {
        newDonationLabel.setVisible(true);
        donorsListView.setVisible(true);
        searchBar.setVisible(true);
        caseInput.setVisible(true);
        caseLabel.setVisible(true);
        nameInput.setVisible(true);
        nameLabel.setVisible(true);
        addressInput.setVisible(true);
        addressLabel.setVisible(true);
        phoneNumberInput.setVisible(true);
        phoneLabel.setVisible(true);
        sumInput.setVisible(true);
        sumLabel.setVisible(true);
        addDonationButton.setVisible(true);
    }

    private void hideDonationFields() {
        newDonationLabel.setVisible(false);
        donorsListView.setVisible(false);
        searchBar.setVisible(false);
        caseInput.setVisible(false);
        caseLabel.setVisible(false);
        nameInput.setVisible(false);
        nameLabel.setVisible(false);
        addressInput.setVisible(false);
        addressLabel.setVisible(false);
        phoneNumberInput.setVisible(false);
        phoneLabel.setVisible(false);
        sumInput.setVisible(false);
        sumLabel.setVisible(false);
        addDonationButton.setVisible(false);
    }

    public void switchToLoginPage(MouseEvent event) throws IOException, TeledonException {
        service.logout(ownerUser, this);
        FXMLLoader loader = new FXMLLoader(StartApplicationFX.class.getResource("/view/LoginView.fxml"));
        AnchorPane root = loader.load();

        LogInController loginController = loader.getController();
        loginController.setService(service);

        FXMLLoader hloader = new FXMLLoader(StartApplicationFX.class.getResource("/view/HomeView.fxml"));
        AnchorPane hroot = hloader.load();
        HomeController homeController = hloader.getController();
        homeController.setService(service);

        loginController.setHomeController(homeController);
        loginController.setParent(hroot);

        stage =(Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void handleAddDonationButton(ActionEvent event) throws TeledonException {
        String donorName = nameInput.getText();
        String donorAddress = addressInput.getText();
        String donorPhoneNumber = phoneNumberInput.getText();
        Float sum = Float.parseFloat(sumInput.getText());
        Donor donor = service.findDonorByName(donorName);
        int donorId;
        if(donor != null){
            donorId = donor.getId();
        }
        else{
            Donor newDonor = new Donor(0, donorName, donorAddress, donorPhoneNumber);
            service.addDonor(newDonor);
            Donor addedDonor = service.findDonorByName(donorName);
            donorId = addedDonor.getId();
        }
        Donation donation = new Donation(0, myCase.getId(), donorId, sum);
        service.addDonation(donation);
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, null, "Donation added succesfully!");
//        initWelcome();
//        initList();
    }


    public void initList() throws TeledonException {
        donors = (List<Donor>) service.getAllDonors();
        ObservableList<String> items = FXCollections.observableArrayList();
        if(!donors.isEmpty()){
            donors.forEach(x ->{
                items.add(x.getId() + " ; " + x.getDonorName() + " ; " + x.getDonorAddress() + " ; " + x.getDonorPhoneNumber());
            });
        }
//        donorsListView.setItems(items);
        FilteredList<String> filteredList = new FilteredList<>(items, b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(donor -> {
                if (newValue == null || newValue.isEmpty())
                    return true;

                String lowerCaseFilter = newValue.toLowerCase();
                String[] splited = donor.split(" ; ");
                String name = splited[1];

                if(name.toLowerCase().contains(lowerCaseFilter))
                    return true;
                return false;
            });
        });
        donorsListView.setItems(filteredList);
    }

    public void handleDonorsSelection(MouseEvent event){
        String string = donorsListView.getSelectionModel().getSelectedItem().toString();
        String[] splited = string.split(" ; ");
        nameInput.setText(splited[1]);
        addressInput.setText(splited[2]);
        phoneNumberInput.setText(splited[3]);
    }

    public void handleNewDonation(ActionEvent event) throws TeledonException {
        //showDonationFields();
        initList();
        //caseInput.setText(myCase.getCaseName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @Override
    public void notifyCaseUpdated(Case caseUpdated) throws Exception {
        Platform.runLater(() -> {
            try{
                System.out.println("Notifying case updated");
                initWelcome();
            }catch (Exception e){
                System.err.println(e.getMessage());
            }
        });
    }

    @Override
    public void notifyDonorAdded(Donor donor) throws Exception {
        Platform.runLater(() -> {
            try{
                System.out.println("Notifying donor added");
                initList();
            }catch (Exception e){
                System.err.println(e.getMessage());
            }
        });
    }
}
