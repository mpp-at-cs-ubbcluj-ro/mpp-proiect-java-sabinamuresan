package org.example.model;

public class Donation extends Entity<Integer>{
    private int idCase;
    private int idDonor;
    private float amount;

    public Donation(int id, int idCase, int idDonor, float amount) {
        super(id);
        this.idCase = idCase;
        this.idDonor = idDonor;
        this.amount = amount;

    }

    public int getIdCase() {
        return idCase;
    }

    public void setCaseToDonate(int id) {
        this.idCase = id;
    }

    public int getIdDonor() {
        return idDonor;
    }

    public void setDonor(int idDonor) {
        this.idDonor = idDonor;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Donation{" +
                "idCase=" + idCase +
                ", idDonor=" + idDonor +
                ", amount=" + amount +
                '}';
    }
}
