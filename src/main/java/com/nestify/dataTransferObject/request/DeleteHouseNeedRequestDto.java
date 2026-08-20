package com.nestify.dataTransferObject.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteHouseNeedRequestDto {
	private Long houseNeedId;
	private Long userId;
	private Long houseId;
}
