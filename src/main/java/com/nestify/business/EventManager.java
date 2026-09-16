package com.nestify.business;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nestify.dataAccess.EventRepository;
import com.nestify.dataTransferObject.request.DeleteEventRequestDto;
import com.nestify.dataTransferObject.request.SaveEventRequestDto;
import com.nestify.dataTransferObject.request.UpdateEventRequestDto;
import com.nestify.dataTransferObject.response.GetEventByIdResponseDto;
import com.nestify.entities.Event;
import com.nestify.entities.EventCategory;
import com.nestify.entities.House;
import com.nestify.entities.User;
import com.nestify.helpers.EventCategoryServiceHelper;
import com.nestify.helpers.EventServiceHelper;
import com.nestify.helpers.HouseServiceHelper;
import com.nestify.helpers.UserServiceHelper;
import com.nestify.mapper.EventMapper;
import com.nestify.policies.EventPolicy;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EventManager implements EventService{
	private final EventRepository eventRepository;
	private final EventServiceHelper eventServiceHelper;
	private final EventMapper eventMapper;
	private final EventPolicy eventPolicy;
	private final HouseServiceHelper houseServiceHelper;
	private final UserServiceHelper userServiceHelper;
	private final EventCategoryServiceHelper eventCategoryServiceHelper;

	@Override
	public List<GetEventByIdResponseDto> getEventsFromHouse(Long houseId, Long actingUserId) {
		House house = houseServiceHelper.getHouseOrThrow(houseId);
		eventPolicy.validateEventOperation(house, actingUserId);
		
		List<GetEventByIdResponseDto> events = house.getEvents()
													.stream()
													.map(event -> eventMapper.toGetEventByIdResponseDto(event))
													.toList();
		return events;
	}

	/**
	 * Adds a new event to a house. If eventCategoryId is null, a default category is assigned.
	 *
	 * @param eventRequestDto DTO containing event creation details
	 * @return response DTO of the created event
	 */
	@Override
	@Transactional
	public GetEventByIdResponseDto addEvent(SaveEventRequestDto eventRequestDto) {
		eventPolicy.validateEventCreation(eventRequestDto.getAssignedUserId());

		User user = userServiceHelper.getUserOrThrow(eventRequestDto.getAssignedUserId());
		House house = houseServiceHelper.getHouseOrThrow(eventRequestDto.getHouseId());
		
		EventCategory eventCategory;
		if (eventRequestDto.getEventCategoryId() != null) {
			eventCategory = eventCategoryServiceHelper.getEventCategoryOrThrow(eventRequestDto.getEventCategoryId());
		} else {
			eventCategory = eventCategoryServiceHelper.getOrCreateDefaultCategory(house);
		}

		eventPolicy.validateEventOperation(house, eventRequestDto.getAssignedUserId());
		
		Event event = new Event();
		event.setTitle(eventRequestDto.getTitle());
		event.setDescription(eventRequestDto.getDescription());
		event.setStartedDate(eventRequestDto.getStartedDate());
		event.setEndDate(eventRequestDto.getEndDate());
		event.setIsAllDay(eventRequestDto.getIsAllDay());
		event.setLocation(eventRequestDto.getLocation());
		event.setAssignedUser(user);
		event.setHouse(house);
		event.setEventCategory(eventCategory);
		
		Event savedEvent = eventRepository.save(event);
		
		return eventMapper.toGetEventByIdResponseDto(savedEvent);
	}

	/**
	 * Updates an existing event. If eventCategoryId is null, existing category is retained or default assigned.
	 *
	 * @param eventId ID of the event to update
	 * @param eventRequestDto DTO containing updated event details
	 * @return response DTO of the updated event
	 */
	@Override
	@Transactional
	public GetEventByIdResponseDto updateEvent(Long eventId, UpdateEventRequestDto eventRequestDto) {
		User user = userServiceHelper.getUserOrThrow(eventRequestDto.getAssignedUserId());
		House house = houseServiceHelper.getHouseOrThrow(eventRequestDto.getHouseId());
		
		Event event = eventServiceHelper.getEventOrThrow(eventId);

		EventCategory eventCategory;
		if (eventRequestDto.getEventCategoryId() != null) {
			eventCategory = eventCategoryServiceHelper.getEventCategoryOrThrow(eventRequestDto.getEventCategoryId());
		} else if (event.getEventCategory() != null) {
			eventCategory = event.getEventCategory();
		} else {
			eventCategory = eventCategoryServiceHelper.getOrCreateDefaultCategory(house);
		}

		eventPolicy.validateEventOperation(house, eventRequestDto.getAssignedUserId());
		
		event.setTitle(eventRequestDto.getTitle());
		event.setDescription(eventRequestDto.getDescription());
		event.setStartedDate(eventRequestDto.getStartedDate());
		event.setEndDate(eventRequestDto.getEndDate());
		event.setIsAllDay(eventRequestDto.getIsAllDay());
		event.setLocation(eventRequestDto.getLocation());
		event.setAssignedUser(user);
		event.setHouse(house);
		event.setEventCategory(eventCategory);
		
		Event savedEvent = eventRepository.save(event);
		
		return eventMapper.toGetEventByIdResponseDto(savedEvent);
	}

	@Override
	public void deleteEvent(DeleteEventRequestDto eventRequestDto) {
		House house = houseServiceHelper.getHouseOrThrow(eventRequestDto.getHouseId());
		Event event = eventServiceHelper.getEventOrThrow(eventRequestDto.getEventId());

		eventPolicy.validateEventOperation(house, eventRequestDto.getUserId());
		
		
		eventRepository.delete(event);
		
	}
	
	
}
