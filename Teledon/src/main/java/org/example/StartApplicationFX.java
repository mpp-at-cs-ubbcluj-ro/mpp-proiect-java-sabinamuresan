package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.controller.LogInController;
import org.example.model.Case;
import org.example.model.Donor;
import org.example.repository.*;
import org.example.service.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static java.lang.System.out;

public class StartApplicationFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Properties props=new Properties();
        try {
            props.load(new FileReader("bd.config.properties"));
            out.println(props);
        } catch (IOException e) {
            out.println("Cannot find bd.config.properties " + e);
        }
        ICaseRepository caseRepository = new CaseDbRepository(props);
        IDonationRepository donationRepository = new DonationDbRepository(props);
        IDonorRepository donorRepository = new DonorDbRepository(props);
        IVolunteerRepository volunteerRepository = new VolunteerDbRepository(props);
        Service service = new Service(caseRepository, donationRepository, donorRepository, volunteerRepository);
        Donor donor = new Donor(0, "Liam Moldovan", "Costache Negruzzi 5", "0756221112");
        Donor addedDonor = service.addDonor(donor);
        System.out.println(addedDonor.getId());

        FXMLLoader loader = new FXMLLoader(StartApplicationFX.class.getResource("/view/LoginView.fxml"));
        AnchorPane root = loader.load();

        LogInController loginController = loader.getController();
        loginController.setService(service);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }
}
