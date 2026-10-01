package com.nestify.dataTransferObject.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaveEventRequestDto {
	
	@NotBlank(message = "Başlık boş olamaz")
	private String title;
	
	private String description;
	
	@NotNull(message = "Başlangıç tarihi belirtilmeli")
	private LocalDateTime startedDate;
	
	private LocalDateTime endDate;
	
	private Boolean isAllDay = false;
	
	private String location;
	
	private Long eventCategoryId;
	
	private Long assignedUserId;
}
