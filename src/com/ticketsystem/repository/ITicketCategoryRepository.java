package com.ticketsystem.repository;

import java.util.List;
import java.util.UUID;

public interface ITicketCategoryRepository<T, ID> extends IRepository<T, ID>{
    List<T> findAllByEvent(UUID eventId);
}
