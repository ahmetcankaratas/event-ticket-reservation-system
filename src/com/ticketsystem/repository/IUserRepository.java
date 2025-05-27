package com.ticketsystem.repository;

import java.util.Optional;

public interface IUserRepository<T, ID> extends IRepository<T, ID> {
    Optional<T> findByUsername(String username);
}
