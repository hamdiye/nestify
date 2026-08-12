package com.nestify.dataTransferObject.response;

import java.time.LocalDateTime;

import com.nestify.entities.enums.MemberRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSummaryForHouseDto {
	private Long id;
	private String name;
	private String email;
	private LocalDateTime joinedAt;
	
	private MemberRole role;
}
