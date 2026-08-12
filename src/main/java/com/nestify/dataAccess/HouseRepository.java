package com.nestify.dataAccess;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nestify.entities.House;

public interface HouseRepository extends JpaRepository<House, Long>{

}
