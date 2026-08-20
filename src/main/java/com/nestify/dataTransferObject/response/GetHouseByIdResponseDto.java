package com.nestify.dataTransferObject.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.nestify.entities.HouseMember;
import com.nestify.entities.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetHouseByIdResponseDto {
	private Long id;
	private String title;
	private String address;
	private String city;
	private String inviteCode;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Set<HouseMember> members;
	
	public List<UserSummaryForHouseDto> getMembers(){
		List<UserSummaryForHouseDto> users = new ArrayList<>();
		for(HouseMember member : members ) {
			User user = member.getUser();
			
			users.add( new UserSummaryForHouseDto(
						user.getId(),
						user.getName(),
						user.getEmail(),
						member.getJoinedAt(),
						member.getMemberRole()
					));
		}
		
		return users;
	
	}
}
