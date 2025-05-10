package com.ticketsystem.repository;

import java.util.List;

/**
 * Interface for data persistence operations.
 */
public interface DataManager<T> {
    void save(T item);
    void saveAll(List<T> items);
    T getById(String id);
    List<T> getAll();
    void delete(String id);
    void deleteAll();
} 