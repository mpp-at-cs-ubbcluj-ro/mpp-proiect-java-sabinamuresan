package org.example.repository;

import org.example.model.Entity;

public interface IRepository<ID,E extends Entity<ID>>{
    E findOne(ID id);
    Iterable<E> getAll();
    E add(E entity);
    E delete(ID id);
    E update(E entity);
}
