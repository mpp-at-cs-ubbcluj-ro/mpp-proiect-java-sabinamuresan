package org.example.dto;

import java.io.Serializable;

public class DonorDTO implements Serializable {
    private String name;

    public DonorDTO(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
