package org.example.protobuffprotocol;
import org.example.model.Case;
import org.example.model.Donation;
import org.example.model.Donor;
import org.example.protobuffprotocol.TeledonProtobufs;
import org.example.model.Volunteer;

import java.util.ArrayList;

public class ProtoUtils {
    public static TeledonProtobufs.TeledonRequest createLoginRequest(String username, String password){
        TeledonProtobufs.TeledonRequest request = TeledonProtobufs.TeledonRequest.newBuilder().setType(TeledonProtobufs.TeledonRequest.Type.LOGIN).setUsername(username).setPassword(password).build();
        return request;
    }

    public static TeledonProtobufs.TeledonRequest createLogoutRequest(Volunteer volunteer){
        TeledonProtobufs.Volunteer volunteerDto = TeledonProtobufs.Volunteer.newBuilder().setId(volunteer.getId()).setUsername(volunteer.getUsername()).setPassword(volunteer.getPassword()).build();
        TeledonProtobufs.TeledonRequest request = TeledonProtobufs.TeledonRequest.newBuilder().setType(TeledonProtobufs.TeledonRequest.Type.LOGOUT).setUser(volunteerDto).build();
        return request;
    }

    public static TeledonProtobufs.TeledonRequest createGetCasesRequest(){
        TeledonProtobufs.TeledonRequest request = TeledonProtobufs.TeledonRequest.newBuilder().setType(TeledonProtobufs.TeledonRequest.Type.GET_CASES).build();
        return request;
    }

    public static TeledonProtobufs.TeledonRequest createGetDonorsRequest(){
        TeledonProtobufs.TeledonRequest request = TeledonProtobufs.TeledonRequest.newBuilder().setType(TeledonProtobufs.TeledonRequest.Type.GET_DONORS).build();
        return request;
    }

    public static TeledonProtobufs.TeledonRequest createGetCaseRequest(int id){
        TeledonProtobufs.TeledonRequest request = TeledonProtobufs.TeledonRequest.newBuilder().setType(TeledonProtobufs.TeledonRequest.Type.GET_CASE).setCaseId(id).build();
        return request;
    }

    public static TeledonProtobufs.TeledonRequest createGetDonorByNameRequest(String name){
        TeledonProtobufs.TeledonRequest request = TeledonProtobufs.TeledonRequest.newBuilder().setType(TeledonProtobufs.TeledonRequest.Type.GET_DONOR_BY_NAME).setDonorName(name).build();
        return request;
    }

    public static TeledonProtobufs.TeledonRequest createUpdateSumInCaseRequest(int idCase, float amount){
        TeledonProtobufs.TeledonRequest request = TeledonProtobufs.TeledonRequest.newBuilder().setType(TeledonProtobufs.TeledonRequest.Type.UPDATE_SUM_IN_CASE).setCaseId(idCase).setAmount(amount).build();
        return request;
    }

    public static TeledonProtobufs.TeledonRequest createAddDonationRequest(Donation donation){
        TeledonProtobufs.Donation donationDto = TeledonProtobufs.Donation.newBuilder().setDonationId(donation.getId()).setCaseId(donation.getIdCase()).setDonorId(donation.getIdDonor()).setAmount(donation.getAmount()).build();
        TeledonProtobufs.TeledonRequest request = TeledonProtobufs.TeledonRequest.newBuilder().setType(TeledonProtobufs.TeledonRequest.Type.ADD_DONATION).setDonation(donationDto).build();
        return request;
    }

    public static TeledonProtobufs.TeledonRequest createAddDonorRequest(Donor donor){
        TeledonProtobufs.Donor donorDto = TeledonProtobufs.Donor.newBuilder().setDonorId(donor.getId()).setName(donor.getDonorName()).setAddress(donor.getDonorAddress()).setPhoneNumber(donor.getDonorPhoneNumber()).build();
        TeledonProtobufs.TeledonRequest request = TeledonProtobufs.TeledonRequest.newBuilder().setType(TeledonProtobufs.TeledonRequest.Type.ADD_DONOR).setDonor(donorDto).build();
        return request;
    }

    public static String getError(TeledonProtobufs.TeledonResponse response){
        String errorMessage=response.getError();
        return errorMessage;
    }

    public static Volunteer getUser(TeledonProtobufs.TeledonResponse response){
        Volunteer volunteer = new Volunteer(0, "", "");
        volunteer.setId(response.getVolunteer().getId());
        volunteer.setUsername(response.getVolunteer().getUsername());
        volunteer.setPassword(response.getVolunteer().getPassword());
        return volunteer;
    }

    public static Case getCase(TeledonProtobufs.TeledonResponse response){
        Case myCase = new Case(0, "", 0);
        myCase.setId(response.getCase().getCaseId());
        myCase.setCaseName(response.getCase().getName());
        myCase.setSum(response.getCase().getSum());
        return myCase;
    }


    public static Donor getDonor(TeledonProtobufs.TeledonResponse response){
        Donor donor = new Donor(0, "", "", "");
        donor.setId(response.getDonor().getDonorId());
        donor.setDonorName(response.getDonor().getName());
        donor.setDonorAddress(response.getDonor().getAddress());
        donor.setDonorPhoneNumber(response.getDonor().getPhoneNumber());
        return donor;
    }

    public static ArrayList<Case> getCases(TeledonProtobufs.TeledonResponse response){
        ArrayList<Case> cases = new ArrayList<>();
        for(TeledonProtobufs.Case caseDto : response.getCasesList()){
            Case myCase = new Case(0, "", 0);
            myCase.setId(caseDto.getCaseId());
            myCase.setCaseName(caseDto.getName());
            myCase.setSum(caseDto.getSum());
            cases.add(myCase);
        }
        return cases;
    }

    public static ArrayList<Donor> getDonors(TeledonProtobufs.TeledonResponse response){
        ArrayList<Donor> donors = new ArrayList<>();
        for(TeledonProtobufs.Donor donorDto : response.getDonorsList()){
            Donor donor = new Donor(0, "", "", "");
            donor.setId(donorDto.getDonorId());
            donor.setDonorName(donorDto.getName());
            donor.setDonorAddress(donorDto.getAddress());
            donor.setDonorPhoneNumber(donorDto.getPhoneNumber());
            donors.add(donor);
        }
        return donors;
    }


}
