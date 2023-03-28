package org.example;

import org.example.model.Donation;
import org.example.model.Donor;
import org.example.repository.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static java.lang.System.*;

public class Main {
    public static void main(String[] args) {
        Properties props=new Properties();
        try {
            props.load(new FileReader("bd.config.properties"));
            out.println(props);
        } catch (IOException e) {
            out.println("Cannot find bd.config.properties " + e);
        }
        ICaseRepository caseRepository = new CaseDbRepository(props);
        caseRepository.getAll().forEach(out::println);
        System.out.println(caseRepository.findOne(1));
        IDonationRepository donationRepository = new DonationDbRepository(props);
        donationRepository.add(new Donation(0,2,2,760));
        donationRepository.getAll().forEach(out::println);
        IDonorRepository donorRepository = new DonorDbRepository(props);
        donorRepository.add(new Donor(0, "Marius Moldovan", "Bucuresti 22", "0754266798"));
        donorRepository.getDonorsForCase(caseRepository.findOne(1)).forEach(out::println);
        System.out.println(donorRepository.findByName("Carmina Pop"));
        IVolunteerRepository volunteerRepository = new VolunteerDbRepository(props);
        System.out.println(volunteerRepository.findAccount("sabinamuresan", "parola"));
    }
}