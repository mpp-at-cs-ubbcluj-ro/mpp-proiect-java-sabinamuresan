package org.example;

import org.example.repository.*;
import org.example.server.Service;
import org.example.services.IService;
import org.example.utils.AbstractServer;
import org.example.utils.TeledonRpcConcurrentServer;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort = 55555;

    public StartRpcServer(){

    }
    public static void main(String[] args) {

        Properties serverProps = new Properties();

        try{
            serverProps.load(StartRpcServer.class.getResourceAsStream("/teledonserver.properties"));
            System.out.println("Server properties set");
            serverProps.list(System.out);
        } catch (IOException e){
            System.err.println("Cannot find teledonserver.properties " + e);
            return;
        }

        ICaseRepository caseRepository = new CaseDbRepository(serverProps);
        IDonationRepository donationRepository = new DonationDbRepository(serverProps);
        IDonorRepository donorRepository = new DonorDbRepository(serverProps);
        IVolunteerRepository volunteerRepository = new VolunteerDbRepository(serverProps);
        IService service = new Service(caseRepository, donationRepository, donorRepository, volunteerRepository);

        int teledonServerPort = defaultPort;

        try{
            teledonServerPort = Integer.parseInt(serverProps.getProperty("teledon.server.port"));
        }catch (NumberFormatException e){
            System.err.println("Wrong  Port Number" + e.getMessage());
            System.err.println("Using default port " + defaultPort);
        }

        System.out.println("Starting server on port: " + teledonServerPort);
        AbstractServer server = new TeledonRpcConcurrentServer(teledonServerPort, service);

        try{
            server.start();
        }catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        } finally {
            try {
                server.stop();
            } catch (ServerException e) {
                System.err.println("Error stopping server " + e.getMessage());
            }

        }

    }
}