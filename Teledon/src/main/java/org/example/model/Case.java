package org.example.model;

public class Case extends Entity<Integer>{
    private String caseName;
    private float sum;
    public Case(int id, String caseName, float sum) {
        super(id);
        this.caseName = caseName;
        this.sum = sum;
    }

    public void addToSum(int id, float amount){
        sum += amount;
    }

    public float getSum() {
        return sum;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    @Override
    public String toString() {
        return "Case{" +
                "caseName='" + caseName + '\'' +
                ", sum=" + sum +
                '}';
    }
}
