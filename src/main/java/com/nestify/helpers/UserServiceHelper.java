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
    
    public void emailIsAlreadyInUse(String email) {
    	if (userRepository.existsByEmail(email)) {
			throw new RuntimeException("Bu email adresi zaten kullanımda!");
		}
    }

    public boolean isUserMemberOfAnyHouse(Long userId) {
        return userRepository.existsByIdAndHouseMembershipsIsNotEmpty(userId);
    }
}
