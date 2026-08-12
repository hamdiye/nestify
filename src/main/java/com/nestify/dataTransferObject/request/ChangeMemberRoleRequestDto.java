package com.nestify.dataTransferObject.request;

import com.nestify.entities.enums.MemberRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeMemberRoleRequestDto {
	private Long userId;
	private MemberRole role;
}
