package com.nestify.dataTransferObject.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetEventByIdResponseDto {
	private Long id;
	private String title;
	private String description;
	private LocalDateTime startedDate;
	private LocalDateTime endDate;
    private Boolean isAllDay = false;
	private String location;
	private Long eventCategoryId;
    private Long houseId;
    private Long assignedUserId;
}
