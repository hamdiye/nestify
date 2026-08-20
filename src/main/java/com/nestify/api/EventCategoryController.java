package com.nestify.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.business.EventCategoryService;
import com.nestify.dataTransferObject.request.DeleteEventCategoryRequestDto;
import com.nestify.dataTransferObject.request.SaveEventCategoryRequestDto;
import com.nestify.dataTransferObject.request.UpdateEventCategoryRequestDto;
import com.nestify.dataTransferObject.response.GetEventCategoryResponseDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/event-categories")
@AllArgsConstructor
public class EventCategoryController {
	private EventCategoryService eventCategoryService;

	@PostMapping("/add")
	public GetEventCategoryResponseDto addEventCategory(@Valid @RequestBody SaveEventCategoryRequestDto eventCategoryDto) {
		return eventCategoryService.addEventCategory(eventCategoryDto);
	}
	@PostMapping("/update")
	public GetEventCategoryResponseDto updateEventCategory(@Valid @RequestBody UpdateEventCategoryRequestDto eventCategoryDto) {
		return eventCategoryService.updateEventCategory(eventCategoryDto);
	}
	@DeleteMapping("/delete")
	public void deleteEventCategory(DeleteEventCategoryRequestDto deleteEventCategory) {
		eventCategoryService.deleteEventCategory(deleteEventCategory);
	}
	
}
