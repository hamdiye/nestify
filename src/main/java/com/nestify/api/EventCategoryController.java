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

import com.nestify.business.EventCategoryService;
import com.nestify.core.UserPrincipal;
import com.nestify.dataTransferObject.request.SaveEventCategoryRequestDto;
import com.nestify.dataTransferObject.request.UpdateEventCategoryRequestDto;
import com.nestify.dataTransferObject.response.GetEventCategoryResponseDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/houses")
@AllArgsConstructor
public class EventCategoryController {
	private final EventCategoryService eventCategoryService;

	@PostMapping("/{houseId}/event-categories")
	public GetEventCategoryResponseDto addEventCategory(
			@PathVariable Long houseId,
			@AuthenticationPrincipal UserPrincipal currentUser,
			@Valid @RequestBody SaveEventCategoryRequestDto eventCategoryDto) {
		return eventCategoryService.addEventCategory(houseId, currentUser.getId(), eventCategoryDto);
	}

	@PutMapping("/{houseId}/event-categories/{eventCategoryId}")
	public GetEventCategoryResponseDto updateEventCategory(
			@PathVariable Long eventCategoryId,
			@PathVariable Long houseId,
			@AuthenticationPrincipal UserPrincipal currentUser,
			@Valid @RequestBody UpdateEventCategoryRequestDto eventCategoryDto) {
		return eventCategoryService.updateEventCategory(houseId, eventCategoryId, currentUser.getId(), eventCategoryDto);
	}

	@DeleteMapping("/{houseId}/event-categories/{eventCategoryId}")
	public void deleteEventCategory(@PathVariable Long houseId, @PathVariable Long eventCategoryId,
			@AuthenticationPrincipal UserPrincipal currentUser) {
		eventCategoryService.deleteEventCategory(houseId, eventCategoryId, currentUser.getId());
	}
	
	@GetMapping("/{houseId}/event-categories")
	public List<GetEventCategoryResponseDto> getAllEventCategoryFromHouse(@PathVariable Long houseId,
			@AuthenticationPrincipal UserPrincipal currentUser) {
		return eventCategoryService.getAllEventCategoryFromHouse(houseId, currentUser.getId());
	}
}
