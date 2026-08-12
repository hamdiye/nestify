package com.nestify.mapper;

import org.springframework.stereotype.Component;

import com.nestify.dataTransferObject.response.GetAllUserResponseDto;
import com.nestify.dataTransferObject.response.GetUserByIdResponseDto;
import com.nestify.dataTransferObject.response.UserSummaryForHouseDto;
import com.nestify.entities.HouseMember;
import com.nestify.entities.User;


@Component
public class UserMapper {
	
	public GetUserByIdResponseDto toGetUserByIdResponseDto(User user) {
		return new GetUserByIdResponseDto(
				user.getId(),
				user.getName(),
				user.getEmail(),
				user.getCreatedAt(),
				user.getUpdatedAt()
				);
	}
	
	public GetAllUserResponseDto toGetAllUserResponseDto(User user) {
		return new GetAllUserResponseDto(
				user.getId(),
				user.getName(),
				user.getEmail(),
				user.getCreatedAt(),
				user.getUpdatedAt()
				);
	}
	
	public UserSummaryForHouseDto toUserSummaryForHouseDto(User user, HouseMember member) {
		return new UserSummaryForHouseDto(
				user.getId(),
				user.getName(),
				user.getEmail(),
				member.getJoinedAt(),
				member.getMemberRole()
				);
	}
}
