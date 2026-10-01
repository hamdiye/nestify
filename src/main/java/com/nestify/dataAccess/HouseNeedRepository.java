package com.nestify.dataAccess;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nestify.entities.HouseNeed;

public interface HouseNeedRepository extends JpaRepository<HouseNeed, Long>{
	List<HouseNeed> findByHouseId(Long houseId);
}
