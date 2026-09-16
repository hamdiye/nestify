package com.nestify.helpers;

import org.springframework.stereotype.Component;

import com.nestify.dataAccess.UserRepository;
import com.nestify.entities.User;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UserServiceHelper {
    private final UserRepository userRepository;
    
    public User getUserOrThrow(Long userId) {
    	return userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + userId));
    }
    
    public User getUserByEmailOrThrow(String email) {
    	return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Email bulunamadı: " + email));
    }
    
    public Boolean emailIsExist(String email) {
    	return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Checks whether the given user is a member of at least one house.
     *
     * @param userId the ID of the user to check
     * @return true if the user has at least one house membership, false otherwise
     */
    public boolean isUserMemberOfAnyHouse(Long userId) {
        return userRepository.existsByIdAndHouseMembershipsIsNotEmpty(userId);
    }
}
