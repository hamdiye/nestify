package com.nestify.mapper;

import org.springframework.stereotype.Component;

import com.nestify.dataTransferObject.response.GetUserByIdResponseDto;
import com.nestify.dataTransferObject.response.HouseMemberResponseDto;
import com.nestify.entities.HouseMember;
import com.nestify.entities.User;

@Component
public class UserMapper {
	
	public GetUserByIdResponseDto toGetUserByIdResponseDto(User user) {
		if (user == null) return null;
		return new GetUserByIdResponseDto(
				user.getId(),
				user.getName(),
				user.getEmail(),
				user.getCreatedAt(),
				user.getUpdatedAt()
				);
	}
	
	public HouseMemberResponseDto toUserSummaryForHouseDto(User user, HouseMember member) {
		if (user == null) return null;
		return new HouseMemberResponseDto(
				user.getId(),
				user.getName(),
				user.getEmail(),
				member.getJoinedAt(),
				member.getMemberRole()
				);
	}
}
