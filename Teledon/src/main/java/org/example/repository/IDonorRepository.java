package org.example.repository;

import org.example.model.Case;
import org.example.model.Donor;

public interface IDonorRepository extends IRepository<Integer, Donor> {
    Iterable<Donor> getDonorsForCase(Case entity);
}
