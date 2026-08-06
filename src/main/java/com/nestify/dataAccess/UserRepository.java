package com.nestify.dataAccess;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nestify.entities.User;



public interface UserRepository extends JpaRepository<User, Long>{
}
