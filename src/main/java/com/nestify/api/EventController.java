package com.nestify.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.business.EventService;
import com.nestify.dataTransferObject.request.DeleteEventRequestDto;
import com.nestify.dataTransferObject.request.SaveEventRequestDto;
import com.nestify.dataTransferObject.request.UpdateEventRequestDto;
import com.nestify.dataTransferObject.response.GetEventByIdResponseDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/events")
@AllArgsConstructor
public class EventController {
	private EventService eventService;
	
	@PostMapping("/add")
	public GetEventByIdResponseDto addEvent(@Valid @RequestBody SaveEventRequestDto eventRequestDto) {
		return eventService.addEvent(eventRequestDto);
	};
	
	@PostMapping("/update/{eventId}")
	public GetEventByIdResponseDto updateEvent(@PathVariable Long eventId, @Valid @RequestBody UpdateEventRequestDto eventRequestDto) {
		return eventService.updateEvent(eventId, eventRequestDto);
	};
	
	@DeleteMapping("/delete")
	public void deleteEvent(@Valid @RequestBody DeleteEventRequestDto eventRequestDto) {
		eventService.deleteEvent(eventRequestDto);
	};
}
