package org.example.services;

import org.example.model.Case;
import org.example.model.Donation;
import org.example.model.Donor;
import org.example.model.Volunteer;

public interface IService {
    public Case findOneCase(int id) throws TeledonException;

    public Iterable<Case> getAllCases() throws TeledonException;

    public Iterable<Donation> getAllDonations() throws TeledonException;

    public Donation addDonation(Donation donation) throws TeledonException;

    public Donor addDonor(Donor donor) throws TeledonException;

    public Iterable<Donor> getAllDonorsForCase(Case myCase);
    public Iterable<Donor> getAllDonors() throws TeledonException;

    public Donor findDonorByName(String name) throws TeledonException;

    public Volunteer login(String username, String password, IObserver client) throws TeledonException;

    public void logout(Volunteer volunteer, IObserver client) throws TeledonException;

    public void updateSumInCase(int caseId, float amount) throws TeledonException;
}
