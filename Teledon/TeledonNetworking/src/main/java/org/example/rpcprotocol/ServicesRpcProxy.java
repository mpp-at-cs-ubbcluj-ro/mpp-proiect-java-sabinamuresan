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
import org.example.services.TeledonException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServicesRpcProxy implements IService {
    private String host;
    private int port;
    private IObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        this.qresponses = new LinkedBlockingQueue();
    }


    @Override
    public Case findOneCase(int id) throws TeledonException {
        Request req=new Request.Builder().type(RequestType.GET_CASE).data(id).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            return (Case) response.data();
        }
        String err=response.data().toString();
        throw new TeledonException(err);
    }

    @Override
    public Iterable<Case> getAllCases() throws TeledonException {
        Request req=new Request.Builder().type(RequestType.GET_CASES).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            return (Iterable<Case>) response.data();
        }
        String err=response.data().toString();
        throw new TeledonException(err);
    }

    @Override
    public Iterable<Donation> getAllDonations() throws TeledonException {
        Request req=new Request.Builder().type(RequestType.GET_DONATIONS).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            return (Iterable<Donation>) response.data();
        }
        String err=response.data().toString();
        throw new TeledonException(err);
    }

    @Override
    public Donation addDonation(Donation donation) throws TeledonException{
        Request req=new Request.Builder().type(RequestType.ADD_DONATION).data(donation).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            return (Donation) response.data();
        }

        String err=response.data().toString();
        throw new TeledonException(err);
    }

    @Override
    public Donor addDonor(Donor donor) throws TeledonException {
        Request req=new Request.Builder().type(RequestType.ADD_DONOR).data(donor).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            return (Donor) response.data();
        }

        String err=response.data().toString();
        throw new TeledonException(err);

    }

    @Override
    public Iterable<Donor> getAllDonorsForCase(Case myCase) {
        return null;
    }

    @Override
    public Iterable<Donor> getAllDonors() throws TeledonException {
        Request req=new Request.Builder().type(RequestType.GET_DONORS).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            return (Iterable<Donor>) response.data();
        }
        String err=response.data().toString();
        throw new TeledonException(err);
    }

    @Override
    public Donor findDonorByName(String name) throws TeledonException {
        DonorDTO donorDTO= new DonorDTO(name);
        Request req=new Request.Builder().type(RequestType.GET_DONOR_BY_NAME).data(donorDTO).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            return (Donor) response.data();
        }
        String err=response.data().toString();
        throw new TeledonException(err);
    }

    @Override
    public Volunteer login(String username, String password, IObserver client) throws TeledonException {
        initializeConnection();
        this.client=client;
        VolunteerDTO voluntarDTO= new VolunteerDTO(username,password);
        Request req=new Request.Builder().type(RequestType.LOGIN).data(voluntarDTO).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            return (Volunteer) response.data();
        }

        String err=response.data().toString();
        closeConnection();
        throw new TeledonException(err);
    }

    @Override
    public void logout(Volunteer volunteer, IObserver client) throws TeledonException {
        Request req=new Request.Builder().type(RequestType.LOGOUT).data(volunteer.getId()).build();
        sendRequest(req);
        Response response=readResponse();
        closeConnection();
        if (response.type()== ResponseType.OK){
            return;
        }
        String err=response.data().toString();
        throw new TeledonException(err);
    }

    @Override
    public void updateSumInCase(int caseId, float amount) throws TeledonException {
        CaseDTO caseDTO = new CaseDTO(caseId, amount);
        Request req=new Request.Builder().type(RequestType.UPDATE_SUM_IN_CASE).data(caseDTO).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            return;
        }
        String err=response.data().toString();
        throw new TeledonException(err);
    }

    private void closeConnection() {
        this.finished = true;

        try {
            this.input.close();
            this.output.close();
            this.connection.close();
            this.client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request) throws TeledonException {
        try {
            this.output.writeObject(request);
            this.output.flush();
        } catch (IOException e) {
            throw new TeledonException("Error sending object " + e);
        }
    }

    private Response readResponse() throws TeledonException {
        Response response = null;

        try {
            response = (Response)this.qresponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response;
    }

    private void initializeConnection() throws TeledonException {
        try {
            this.connection = new Socket(this.host, this.port);
            this.output = new ObjectOutputStream(this.connection.getOutputStream());
            this.output.flush();
            this.input = new ObjectInputStream(this.connection.getInputStream());
            this.finished = false;
            this.startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleUpdate(Response response){
        if (response.type()== ResponseType.MADE_DONATION){
            System.out.println("Donation made");
            try {
                Case aCase = (Case) response.data();
                client.notifyCaseUpdated(aCase);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        if (response.type()== ResponseType.ADDED_DONOR){
            System.out.println("Donor added");
            try {
                Donor donor = (Donor) response.data();
                client.notifyDonorAdded(donor);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean isUpdate(Response response){
        return response.type() == ResponseType.MADE_DONATION || response.type() == ResponseType.ADDED_DONOR;
    }
    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private class ReaderThread implements Runnable {
        private ReaderThread() {
        }

        public void run() {
            while(!ServicesRpcProxy.this.finished) {
                try {
                    Object response = ServicesRpcProxy.this.input.readObject();
                    System.out.println("response received " + response);
                    if (ServicesRpcProxy.this.isUpdate((Response)response)) {
                        ServicesRpcProxy.this.handleUpdate((Response)response);
                    } else {
                        try {
                            ServicesRpcProxy.this.qresponses.put((Response)response);
                        } catch (InterruptedException var3) {
                            var3.printStackTrace();
                        }
                    }
                } catch (IOException var4) {
                    System.out.println("Reading error " + var4);
                } catch (ClassNotFoundException var5) {
                    System.out.println("Reading error " + var5);
                }
            }

        }
    }
}


