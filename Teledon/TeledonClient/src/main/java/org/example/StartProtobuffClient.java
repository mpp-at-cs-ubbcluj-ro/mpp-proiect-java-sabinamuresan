package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.controller.HomeController;
import org.example.controller.LogInController;
import org.example.protobuffprotocol.ProtoProxy;
import org.example.rpcprotocol.ServicesRpcProxy;
import org.example.services.IService;

import java.io.IOException;
import java.util.Properties;

public class StartProtobuffClient extends Application {
    private static int defaultPort = 55555;
    private static String defaultServer = "localhost";

    @Override
    public void start(Stage stage) throws Exception {
        Properties props=new Properties();
        try {
            props.load(StartApplicationFX.class.getResourceAsStream("/teledonclient.properties"));
            System.out.println("Client properties set.");
            props.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find teledonclient.properties " + e);
        }
        String serverIP = props.getProperty("teledon.server.host",defaultServer);
        int serverPort = defaultPort;
        try{
            serverPort = Integer.parseInt(props.getProperty("teledon.server.port"));
        }catch (NumberFormatException e){
            System.err.println("Wrong port number " + e.getMessage());
            System.out.println("Using default port: " + defaultPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);

        IService server = new ProtoProxy(serverIP, serverPort);
        FXMLLoader loader = new FXMLLoader(StartApplicationFX.class.getResource("/view/LoginView.fxml"));
        AnchorPane root = loader.load();

        LogInController loginController = loader.getController();
        loginController.setService(server);

        FXMLLoader hloader = new FXMLLoader(StartApplicationFX.class.getResource("/view/HomeView.fxml"));
        AnchorPane hroot = hloader.load();
        HomeController homeController = hloader.getController();
        homeController.setService(server);

        loginController.setHomeController(homeController);
        loginController.setParent(hroot);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.show();
    }
}
