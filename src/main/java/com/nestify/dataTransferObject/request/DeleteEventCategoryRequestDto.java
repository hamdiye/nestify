package com.nestify.dataTransferObject.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteEventCategoryRequestDto {
	private Long actingUserId;
	private Long houseId;
	private Long categoryId;
}
