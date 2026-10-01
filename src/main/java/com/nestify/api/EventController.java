package com.nestify.api;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.business.EventService;
import com.nestify.core.UserPrincipal;
import com.nestify.dataTransferObject.request.SaveEventRequestDto;
import com.nestify.dataTransferObject.request.UpdateEventRequestDto;
import com.nestify.dataTransferObject.response.GetEventByIdResponseDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/houses")
@AllArgsConstructor
public class EventController {
	private EventService eventService;
	
	@PostMapping("/{houseId}/events")
	public GetEventByIdResponseDto addEvent(@PathVariable Long houseId,
			@AuthenticationPrincipal UserPrincipal currentUser,
			@Valid @RequestBody SaveEventRequestDto eventRequestDto) {
		return eventService.addEvent(houseId, currentUser.getId(), eventRequestDto);
	}
	
	@PutMapping("/{houseId}/events/{eventId}")
	public GetEventByIdResponseDto updateEvent(@PathVariable Long houseId, 
			@PathVariable Long eventId,
			@AuthenticationPrincipal UserPrincipal currentUser,
			@Valid @RequestBody UpdateEventRequestDto eventRequestDto) {
		return eventService.updateEvent(houseId, eventId, currentUser.getId(), eventRequestDto);
	}
	
	@DeleteMapping("/{houseId}/events/{eventId}")
	public void deleteEvent(@PathVariable Long eventId,
			@PathVariable Long houseId,
			@AuthenticationPrincipal UserPrincipal currentUser) {
		eventService.deleteEvent(houseId, eventId, currentUser.getId());
	}
	
	@GetMapping("/{houseId}/events")
	public List<GetEventByIdResponseDto> getEventsFromHouse(@PathVariable Long houseId,
			@AuthenticationPrincipal UserPrincipal currentUser) {
		return eventService.getEventsFromHouse(houseId, currentUser.getId());
	}
}