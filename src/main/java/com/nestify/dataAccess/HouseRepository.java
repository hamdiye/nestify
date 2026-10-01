package com.nestify.dataAccess;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nestify.entities.House;

public interface HouseRepository extends JpaRepository<House, Long>{
	Optional<House> findByInviteCode(String inviteCode);
	@Query("SELECT h FROM House h LEFT JOIN FETCH h.members m LEFT JOIN FETCH m.user WHERE h.id = :id")
	Optional<House> findByIdWithMembers(@Param("id") Long id);
}
