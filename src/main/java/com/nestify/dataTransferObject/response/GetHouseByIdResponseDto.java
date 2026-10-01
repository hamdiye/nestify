package com.nestify.dataTransferObject.response;

import java.time.LocalDateTime;
import java.util.Set;

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
	private Set<HouseMemberResponseDto> members;
	
}
