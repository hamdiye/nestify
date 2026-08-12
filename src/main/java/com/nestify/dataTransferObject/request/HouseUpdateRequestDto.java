package com.nestify.dataTransferObject.request;

import java.util.Set;

import com.nestify.dataTransferObject.response.UserSummaryForHouseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HouseUpdateRequestDto {
	private Long userId;
	private String title;
	private String address;
	private String city;
	
	private Set<UserSummaryForHouseDto> members;
}
