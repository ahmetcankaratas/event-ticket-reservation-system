package com.ticketsystem.repository;

import java.util.List;
import java.util.Optional;

public interface IRepository<T, ID> {
    void save(T obj);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    void update(ID id, T newObj);
}
