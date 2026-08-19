package com.nestify.dataTransferObject.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeleteEventRequestDto {
	private Long eventId;
	private Long userId;
	private Long houseId;
}
