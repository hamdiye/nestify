package com.nestify.dataAccess;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nestify.entities.Event;

public interface EventRepository extends JpaRepository<Event, Long>{

}
