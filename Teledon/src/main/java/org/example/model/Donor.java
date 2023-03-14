package org.example.model;

public class Donor extends Entity<Integer>{

    private String donorName;
    private String donorAddress;
    private String donorPhoneNumber;

    public Donor(int id, String donorName, String donorAddress, String donorPhoneNumber) {
        super(id);
        this.donorName = donorName;
        this.donorAddress = donorAddress;
        this.donorPhoneNumber = donorPhoneNumber;
    }

    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorAddress() {
        return donorAddress;
    }

    public void setDonorAddress(String donorAddress) {
        this.donorAddress = donorAddress;
    }

    public String getDonorPhoneNumber() {
        return donorPhoneNumber;
    }

    public void setDonorPhoneNumber(String donorPhoneNumber) {
        this.donorPhoneNumber = donorPhoneNumber;
    }
}
