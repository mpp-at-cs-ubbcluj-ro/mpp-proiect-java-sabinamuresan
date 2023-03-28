package org.example.repository;

import org.example.model.Volunteer;

public interface IVolunteerRepository extends IRepository<Integer, Volunteer> {
    public Volunteer findAccount(String username, String password);
}
