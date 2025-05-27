package com.ticketsystem.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IEventRepository<T, ID> extends IRepository<T , ID>{
    List<T> findEventsByTimeInterval(LocalDateTime startDate, LocalDateTime endDate);
    List<T> findEventsByOrganizer(UUID organizerId);
}
