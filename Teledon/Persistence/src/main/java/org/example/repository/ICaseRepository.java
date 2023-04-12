package org.example.repository;

import org.example.model.Case;

public interface ICaseRepository extends IRepository<Integer, Case> {
    void updateSum(int id, float sum);
}
