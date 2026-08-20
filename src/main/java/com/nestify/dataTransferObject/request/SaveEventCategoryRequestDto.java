package com.nestify.dataTransferObject.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveEventCategoryRequestDto {
	private Long userId;
	private Long houseId;
	private String title;
	private String description;
	private String colorCode;
}
