package com.nestify.dataTransferObject.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetEventCategoryResponseDto {
	private Long id;
	private Long houseId;
	private String title;
	private String description;
	private String colorCode;
}
