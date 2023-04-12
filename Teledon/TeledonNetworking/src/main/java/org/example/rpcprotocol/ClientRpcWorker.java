package org.example.rpcprotocol;

import org.example.dto.CaseDTO;
import org.example.dto.DonorDTO;
import org.example.dto.VolunteerDTO;
import org.example.model.Case;
import org.example.model.Donation;
import org.example.model.Donor;
import org.example.model.Volunteer;
import org.example.services.IObserver;
import org.example.services.IService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ResourceBundle;

public class ClientRpcWorker implements Runnable, IObserver {
    private IService server;
    private Socket connection;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    private static Response okResponse = new Response.Builder().type(ResponseType.OK).build();

    public ClientRpcWorker(IService server, Socket connection){
        this.server = server;
        this.connection = connection;

        try{
            this.output = new ObjectOutputStream(connection.getOutputStream());
            this.output.flush();
            this.input = new ObjectInputStream(connection.getInputStream());
            this.connected = true;
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void run() {
        while(this.connected) {
            try {
                Object request = this.input.readObject();
                Response response = this.handleRequest((Request)request);
                if (response != null) {
                    this.sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            this.input.close();
            this.output.close();
            this.connection.close();
        } catch (IOException e) {
            System.out.println("Error " + e);
        }

    }

    private Response handleRequest(Request request) {
        Response response=null;
        if (request.type()== RequestType.LOGIN){
            System.out.println("Login request "+request.type());
            VolunteerDTO volunteerDTO=(VolunteerDTO) request.data();
            try {
                Volunteer volunteer = server.login(volunteerDTO.getUsername(), volunteerDTO.getPassword(),this);
                response = new Response.Builder().type(ResponseType.OK).data(volunteer).build();
            } catch (Exception e) {
                connected=false;
                response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type()== RequestType.LOGOUT){
            //todo
        }
        if (request.type()== RequestType.GET_CASES){
            System.out.println("GetCases Request ");
            try {
                Iterable<Case> cases =server.getAllCases();
                response = new Response.Builder().type(ResponseType.OK).data(cases).build();
            } catch (Exception e) {
                response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type()== RequestType.GET_DONORS){
            System.out.println("GetDonors Request ");
            try {
                Iterable<Donor> donors =server.getAllDonors();
                response = new Response.Builder().type(ResponseType.OK).data(donors).build();
            } catch (Exception e) {
                response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type()== RequestType.GET_DONOR_BY_NAME){
            System.out.println("GetDonorByName Request ");
            DonorDTO donorDTO=(DonorDTO) request.data();
            try {
                Donor donor = server.findDonorByName(donorDTO.getName());
                response = new Response.Builder().type(ResponseType.OK).data(donor).build();
            } catch (Exception e) {
                response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type()== RequestType.ADD_DONATION){
            System.out.println("AddDonation Request ");
            Donation donation=(Donation) request.data();
            try {
                server.addDonation(donation);
                response = new Response.Builder().type(ResponseType.OK).build();
            } catch (Exception e) {
                response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type()== RequestType.ADD_DONOR){
            System.out.println("AddDonor Request ");
            Donor donor=(Donor) request.data();
            try {
                server.addDonor(donor);
                response = new Response.Builder().type(ResponseType.OK).build();
            } catch (Exception e) {
                response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type()== RequestType.GET_CASE){
            System.out.println("GetCase Request ");
            int idCase=(int) request.data();
            try {
                Case aCase = server.findOneCase(idCase);
                response = new Response.Builder().type(ResponseType.OK).data(aCase).build();
            } catch (Exception e) {
                response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type()== RequestType.UPDATE_SUM_IN_CASE){
            System.out.println("UpdateSumInCase Request ");
            CaseDTO aCase=(CaseDTO) request.data();
            try {
                server.updateSumInCase(aCase.getIdCase(), aCase.getAmount());
                response = new Response.Builder().type(ResponseType.OK).build();
            } catch (Exception e) {
                response = new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        return response;
    }

    private void sendResponse(Response response) throws IOException {
        System.out.println("sending response " + response);
        this.output.writeObject(response);
        this.output.flush();
    }

    static {
        okResponse = (new Response.Builder()).type(ResponseType.OK).build();
    }


    @Override
    public void notifyCaseUpdated(Case caseUpdated) throws Exception {
        try{
            Response response = new Response.Builder().type(ResponseType.MADE_DONATION).data(caseUpdated).build();
            sendResponse(response);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void notifyDonorAdded(Donor donor) throws Exception {
        try{
            Response response = new Response.Builder().type(ResponseType.ADDED_DONOR).data(donor).build();
            sendResponse(response);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
