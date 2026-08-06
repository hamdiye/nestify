package com.nestify.dataTransferObject.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetAllUserResponseDto {

	private Long id;
	private String name;
	private String email;
	
	
}
