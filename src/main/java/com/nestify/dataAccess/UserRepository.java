package com.nestify.dataAccess;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nestify.entities.User;



public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByEmail(String email);

	/**
	 * Checks whether the given user is a member of at least one house.
	 *
	 * @param userId the ID of the user to check
	 * @return true if the user has at least one house membership
	 */
	boolean existsByIdAndHouseMembershipsIsNotEmpty(Long userId);
}

