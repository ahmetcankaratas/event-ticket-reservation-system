package com.ticketsystem.repository;

import java.util.List;
import java.util.UUID;

public interface IReservationRepository<T, ID> extends IRepository<T, ID> {
    List<T> findAllByUser(UUID userId);
}
