package com.nestify.dataAccess;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nestify.entities.HouseNeed;


public interface HouseNeedRepository extends JpaRepository<HouseNeed, Long>{

}
