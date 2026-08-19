package com.nestify.dataTransferObject.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventCategoryRequestDto {
	private Long user_id;
	private Long house_id;
	private String title;
	private String description;
	private String colorCode;
}
