package org.example.model;

public class Donation extends Entity<Integer>{
    private Case caseToDonate;
    private Donor donor;
    private float amount;

    public Donation(int id, Case caseToDonate, Donor donor, float amount) {
        super(id);
        this.caseToDonate = caseToDonate;
        this.donor = donor;
        this.amount = amount;

    }

    public Case getCaseToDonate() {
        return caseToDonate;
    }

    public void setCaseToDonate(Case caseToDonate) {
        this.caseToDonate = caseToDonate;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
