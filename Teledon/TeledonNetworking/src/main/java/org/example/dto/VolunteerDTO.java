package org.example.dto;

import java.io.Serializable;

public class VolunteerDTO implements Serializable {
    private String username;
    private String password;

    public VolunteerDTO(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
