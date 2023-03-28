package org.example.service;

import org.example.model.Case;
import org.example.model.Donation;
import org.example.model.Donor;
import org.example.model.Volunteer;
import org.example.repository.ICaseRepository;
import org.example.repository.IDonationRepository;
import org.example.repository.IDonorRepository;
import org.example.repository.IVolunteerRepository;

public class Service {
    ICaseRepository caseRepository;
    IDonationRepository donationRepository;
    IDonorRepository donorRepository;
    IVolunteerRepository volunteerRepository;

    public Service(ICaseRepository caseRepository, IDonationRepository donationRepository, IDonorRepository donorRepository, IVolunteerRepository volunteerRepository) {
        this.caseRepository = caseRepository;
        this.donationRepository = donationRepository;
        this.donorRepository = donorRepository;
        this.volunteerRepository = volunteerRepository;
    }

    public Case findOneCase(int id){
        return caseRepository.findOne(id);
    }

    public Iterable<Case> getAllCases(){
        return caseRepository.getAll();
    }

    public Iterable<Donation> getAllDonations(){
        return donationRepository.getAll();
    }

    public Donation addDonation(Donation donation){
        return donationRepository.add(donation);
    }

    public Donor addDonor(Donor donor){
        return donorRepository.add(donor);
    }

    public Iterable<Donor> getAllDonorsForCase(Case myCase){
        return donorRepository.getDonorsForCase(myCase);
    }
    public Iterable<Donor> getAllDonors(){
        return donorRepository.getAll();
    }

    public Donor findDonorByName(String name){
        return donorRepository.findByName(name);
    }

    public Volunteer findVolunteerAccount(String username, String password){
        return volunteerRepository.findAccount(username, password);
    }
}
