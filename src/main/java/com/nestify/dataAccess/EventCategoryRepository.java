package com.nestify.dataAccess;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nestify.entities.EventCategory;


public interface EventCategoryRepository extends JpaRepository<EventCategory, Long>{

}
