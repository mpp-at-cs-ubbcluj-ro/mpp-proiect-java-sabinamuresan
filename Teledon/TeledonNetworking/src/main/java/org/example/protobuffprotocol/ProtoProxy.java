package org.example.protobuffprotocol;

import org.example.model.Case;
import org.example.model.Donation;
import org.example.model.Donor;
import org.example.model.Volunteer;
import org.example.rpcprotocol.ServicesRpcProxy;
import org.example.services.IObserver;
import org.example.services.IService;
import org.example.services.TeledonException;
import org.example.protobuffprotocol.TeledonProtobufs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProtoProxy implements IService {
    private String host;
    private int port;

    private IObserver client;

    private InputStream input;
    private OutputStream output;
    private Socket connection;
    private BlockingQueue<TeledonProtobufs.TeledonResponse> qresponses;
    private volatile boolean finished;

    public ProtoProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses=new LinkedBlockingQueue<TeledonProtobufs.TeledonResponse>();
    }
    @Override
    public Case findOneCase(int id) throws TeledonException {
        sendRequest(ProtoUtils.createGetCaseRequest(id));
        TeledonProtobufs.TeledonResponse response = readResponse();
        if (response.getType()== TeledonProtobufs.TeledonResponse.Type.ERROR){
            String err=ProtoUtils.getError(response);
            throw new RuntimeException(err);
        }
        Case myCase = ProtoUtils.getCase(response);
        System.out.println(myCase);
        return myCase;
    }

    @Override
    public Iterable<Case> getAllCases() throws TeledonException {
        sendRequest(ProtoUtils.createGetCasesRequest());
        TeledonProtobufs.TeledonResponse response = readResponse();
        if (response.getType()== TeledonProtobufs.TeledonResponse.Type.ERROR){
            String err=ProtoUtils.getError(response);
            throw new RuntimeException(err);
        }

        ArrayList<Case> cases = ProtoUtils.getCases(response);
        return cases;
    }

    @Override
    public Iterable<Donation> getAllDonations() throws TeledonException {
        return null;
    }

    @Override
    public Donation addDonation(Donation donation) throws TeledonException {
        sendRequest(ProtoUtils.createAddDonationRequest(donation));
        TeledonProtobufs.TeledonResponse response = readResponse();
        if (response.getType()== TeledonProtobufs.TeledonResponse.Type.ERROR){
            String err=ProtoUtils.getError(response);
            throw new RuntimeException(err);
        }
        return null;
    }

    @Override
    public Donor addDonor(Donor donor) throws TeledonException {
        sendRequest(ProtoUtils.createAddDonorRequest(donor));
        TeledonProtobufs.TeledonResponse response = readResponse();
        if (response.getType()== TeledonProtobufs.TeledonResponse.Type.ERROR){
            String err=ProtoUtils.getError(response);
            throw new RuntimeException(err);
        }
        Donor donor1 = ProtoUtils.getDonor(response);
        return donor1;
    }

    @Override
    public Iterable<Donor> getAllDonorsForCase(Case myCase) {
        return null;
    }

    @Override
    public Iterable<Donor> getAllDonors() throws TeledonException {
        sendRequest(ProtoUtils.createGetDonorsRequest());
        TeledonProtobufs.TeledonResponse response = readResponse();
        if (response.getType()== TeledonProtobufs.TeledonResponse.Type.ERROR){
            String err=ProtoUtils.getError(response);
            throw new RuntimeException(err);
        }

        ArrayList<Donor> donors = ProtoUtils.getDonors(response);
        return donors;
    }

    @Override
    public Donor findDonorByName(String name) throws TeledonException {
        sendRequest(ProtoUtils.createGetDonorByNameRequest(name));
        TeledonProtobufs.TeledonResponse response = readResponse();
        Donor donor = null;
        if (response.getType()== TeledonProtobufs.TeledonResponse.Type.ERROR){
            String err=ProtoUtils.getError(response);
        }
        else {
            donor = ProtoUtils.getDonor(response);
        }
        return donor;
    }

    @Override
    public Volunteer login(String username, String password, IObserver client) throws TeledonException {
        initializeConnection();
        sendRequest(ProtoUtils.createLoginRequest(username, password));
        TeledonProtobufs.TeledonResponse response = readResponse();
        if(response.getType() == TeledonProtobufs.TeledonResponse.Type.ERROR){
            String err = ProtoUtils.getError(response);
            closeConnection();
            throw new RuntimeException(err);
        }
        else{
            this.client = client;
            return ProtoUtils.getUser(response);
        }
    }

    @Override
    public void logout(Volunteer volunteer, IObserver client) throws TeledonException {
        sendRequest(ProtoUtils.createLogoutRequest(volunteer));
        TeledonProtobufs.TeledonResponse response = readResponse();
        closeConnection();
        if (response.getType() == TeledonProtobufs.TeledonResponse.Type.ERROR){
            String err = ProtoUtils.getError(response);
            throw new RuntimeException(err);
        }
    }

    @Override
    public void updateSumInCase(int caseId, float amount) throws TeledonException {
        sendRequest(ProtoUtils.createUpdateSumInCaseRequest(caseId, amount));
        TeledonProtobufs.TeledonResponse response = readResponse();
        if (response.getType() == TeledonProtobufs.TeledonResponse.Type.ERROR){
            String err = ProtoUtils.getError(response);
            throw new RuntimeException(err);
        }
    }

    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(TeledonProtobufs.TeledonRequest request)throws TeledonException{
        try {
            System.out.println("Sending request ..."+request);
            //request.writeTo(output);
            request.writeDelimitedTo(output);
            output.flush();
            System.out.println("Request sent.");
        } catch (IOException e) {
            throw new TeledonException("Error sending object "+e);
        }

    }

    private TeledonProtobufs.TeledonResponse readResponse() throws TeledonException{
        TeledonProtobufs.TeledonResponse response=null;
        try{
            response=qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() throws TeledonException{
        try {
            connection=new Socket(host,port);
            output=connection.getOutputStream();
            //output.flush();
            input=connection.getInputStream();     //new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    TeledonProtobufs.TeledonResponse response=TeledonProtobufs.TeledonResponse.parseDelimitedFrom(input);
                    System.out.println("response received "+response);

                    if (isUpdateResponse(response.getType())){
                        handleUpdate(response);
                    }else{
                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }

    private void handleUpdate(TeledonProtobufs.TeledonResponse updateResponse) throws Exception {
        switch (updateResponse.getType()){
            case MADE_DONATION:{
                Case aCase = ProtoUtils.getCase(updateResponse);
                client.notifyCaseUpdated(aCase);
                break;
            }
            case ADDED_DONOR:{
                Donor donor = ProtoUtils.getDonor(updateResponse);
                client.notifyDonorAdded(donor);
                break;
            }

        }

    }

    private boolean isUpdateResponse(TeledonProtobufs.TeledonResponse.Type type){
        switch (type){
            case MADE_DONATION:  return true;
            case ADDED_DONOR: return true;
        }
        return false;
    }
}
