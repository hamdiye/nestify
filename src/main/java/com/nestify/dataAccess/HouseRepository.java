package com.nestify.dataAccess;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nestify.entities.House;

public interface HouseRepository extends JpaRepository<House, Long>{
	Optional<House> findByInviteCode(String inviteCode);
}
