package org.example.server;

import org.example.model.Case;
import org.example.model.Donation;
import org.example.model.Donor;
import org.example.model.Volunteer;
import org.example.repository.ICaseRepository;
import org.example.repository.IDonationRepository;
import org.example.repository.IDonorRepository;
import org.example.repository.IVolunteerRepository;
import org.example.services.IObserver;
import org.example.services.IService;
import org.example.services.TeledonException;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements IService {
    private ICaseRepository caseRepository;
    private IDonationRepository donationRepository;
    private IDonorRepository donorRepository;
    private IVolunteerRepository volunteerRepository;

    private Map<Integer, IObserver> loggedClients;

    private final int defaultThreadsNo=5;

    public Service(ICaseRepository caseRepository, IDonationRepository donationRepository, IDonorRepository donorRepository, IVolunteerRepository volunteerRepository) {
        this.caseRepository = caseRepository;
        this.donationRepository = donationRepository;
        this.donorRepository = donorRepository;
        this.volunteerRepository = volunteerRepository;
        loggedClients = new ConcurrentHashMap<>();
    }

    public synchronized Case findOneCase(int id){
        return caseRepository.findOne(id);
    }

    public synchronized Iterable<Case> getAllCases(){
        return caseRepository.getAll();
    }

    public synchronized Iterable<Donation> getAllDonations(){
        return donationRepository.getAll();
    }

    public synchronized Donation addDonation(Donation donation){
        updateSumInCase(donation.getIdCase(), donation.getAmount());
        return donationRepository.add(donation);
    }

    public synchronized Donor addDonor(Donor donor){
        Donor addedDonor = donorRepository.add(donor);
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        for(IObserver client : loggedClients.values()){
            if(client != null){
                executor.execute(() -> {
                    try{
                        client.notifyDonorAdded(donor);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                });
            }
        }
        executor.shutdown();
        return addedDonor;
    }

    public synchronized Iterable<Donor> getAllDonorsForCase(Case myCase){
        return donorRepository.getDonorsForCase(myCase);
    }
    public synchronized Iterable<Donor> getAllDonors(){
        return donorRepository.getAll();
    }

    public synchronized Donor findDonorByName(String name){
        return donorRepository.findByName(name);
    }

    public synchronized Volunteer login(String username, String password, IObserver client) throws TeledonException {
        Volunteer user =  volunteerRepository.findAccount(username, password);
        if(user != null){
            if(loggedClients.get(user.getId()) != null){
                throw new TeledonException("This volunteer is already logged!");
            }
            loggedClients.put(user.getId(), client);
        }
        else{
            throw new TeledonException("Autentification failed!");
        }
        return user;
    }

    public synchronized void logout(Volunteer volunteer, IObserver client) throws TeledonException{
        IObserver removedClient = loggedClients.remove(volunteer.getId());
        if(removedClient == null){
            throw new TeledonException("Volunteer with id " + volunteer.getId() + " is not logged!");
        }
    }

    public synchronized void updateSumInCase(int caseId, float amount){
        ExecutorService executor = Executors.newFixedThreadPool(defaultThreadsNo);
        Case myCase = findOneCase(caseId);
        float sum = myCase.getSum();
        sum += amount;
        caseRepository.updateSum(caseId, sum);
        for(IObserver client : loggedClients.values()){
            if(client != null) {
                executor.execute(() -> {
                    try {
                        client.notifyCaseUpdated(myCase);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        executor.shutdown();
    }



}
