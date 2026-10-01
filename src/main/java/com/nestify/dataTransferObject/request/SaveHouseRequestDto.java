package com.nestify.dataTransferObject.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveHouseRequestDto {
	private String title;
	private String address;
	private String city;
}
