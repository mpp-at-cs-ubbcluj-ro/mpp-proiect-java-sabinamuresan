package org.example.dto;

import org.example.model.Case;

import java.io.Serializable;

public class CaseDTO implements Serializable {
    int idCase;

    float amount;

    public CaseDTO(int idCase, float amount){
        this.idCase = idCase;
        this.amount = amount;
    }

    public float getAmount() {
        return amount;
    }

    public int getIdCase(){
        return idCase;
    }
}
