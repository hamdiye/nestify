package com.nestify.business;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nestify.dataAccess.EventRepository;
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
import com.nestify.policies.EventCategoryPolicy;
import com.nestify.policies.EventPolicy;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EventManager implements EventService {
	private final EventRepository eventRepository;
	private final EventServiceHelper eventServiceHelper;
	private final EventMapper eventMapper;
	private final EventPolicy eventPolicy;
	private final HouseServiceHelper houseServiceHelper;
	private final UserServiceHelper userServiceHelper;
	private final EventCategoryServiceHelper eventCategoryServiceHelper;
	private final EventCategoryPolicy eventCategoryPolicy;

	@Override
	public List<GetEventByIdResponseDto> getEventsFromHouse(Long houseId, Long actingUserId) {
		House house = houseServiceHelper.getHouseOrThrow(houseId);
		eventPolicy.validateEventOperation(house, actingUserId);
		
		List<GetEventByIdResponseDto> events =  eventRepository.findByHouseId(houseId)
													.stream()
													.map(eventMapper::toGetEventByIdResponseDto)
													.toList();
		return events;
	}

	@Override
	@Transactional
	public GetEventByIdResponseDto addEvent(Long houseId, Long actingUserId, SaveEventRequestDto eventRequestDto) {
		House house = houseServiceHelper.getHouseOrThrow(houseId);
		eventPolicy.validateEventOperation(house, actingUserId);
		eventPolicy.validateEventOperation(house, eventRequestDto.getAssignedUserId());

		User user = userServiceHelper.getUserOrThrow(eventRequestDto.getAssignedUserId());

		EventCategory eventCategory;
		if (eventRequestDto.getEventCategoryId() != null) {
			eventCategory = eventCategoryServiceHelper.getEventCategoryOrThrow(eventRequestDto.getEventCategoryId());
		} else {
			eventCategory = eventCategoryServiceHelper.getOrCreateDefaultCategory(house);
		}
		eventCategoryPolicy.validateEventCategoryBelogsToHouse(eventCategory, houseId);

		Event event = new Event();
		event.setTitle(eventRequestDto.getTitle());
		event.setDescription(eventRequestDto.getDescription());
		event.setStartDateTime(eventRequestDto.getStartedDate());
		event.setEndDateTime(eventRequestDto.getEndDate());
		event.setAllDay(eventRequestDto.getIsAllDay());
		event.setLocation(eventRequestDto.getLocation());
		event.setAssignedUser(user);
		event.setHouse(house);
		event.setEventCategory(eventCategory);
		
		Event savedEvent = eventRepository.save(event);
		
		return eventMapper.toGetEventByIdResponseDto(savedEvent);
	}

	@Override
	@Transactional
	public GetEventByIdResponseDto updateEvent(Long houseId, Long eventId, Long actingUserId, UpdateEventRequestDto eventRequestDto) {
		Event event = eventServiceHelper.getEventOrThrow(eventId);
		eventPolicy.validateEventBelogsToHouse(event, houseId);
				
		House house = houseServiceHelper.getHouseOrThrow(houseId);
		eventPolicy.validateEventOperation(house, actingUserId);
		eventPolicy.validateEventOperation(house, eventRequestDto.getAssignedUserId());

		User user = userServiceHelper.getUserOrThrow(eventRequestDto.getAssignedUserId());

		EventCategory eventCategory;
		if (eventRequestDto.getEventCategoryId() != null) {
			eventCategory = eventCategoryServiceHelper.getEventCategoryOrThrow(eventRequestDto.getEventCategoryId());
		} else if (event.getEventCategory() != null) {
			eventCategory = event.getEventCategory();
		} else {
			eventCategory = eventCategoryServiceHelper.getOrCreateDefaultCategory(house);
		}
		eventCategoryPolicy.validateEventCategoryBelogsToHouse(eventCategory, houseId);
		
		event.setTitle(eventRequestDto.getTitle());
		event.setDescription(eventRequestDto.getDescription());
		event.setStartDateTime(eventRequestDto.getStartedDate());
		event.setEndDateTime(eventRequestDto.getEndDate());
		event.setAllDay(eventRequestDto.getIsAllDay());
		event.setLocation(eventRequestDto.getLocation());
		event.setAssignedUser(user);
		event.setHouse(house);
		event.setEventCategory(eventCategory);
		
		Event savedEvent = eventRepository.save(event);
		
		return eventMapper.toGetEventByIdResponseDto(savedEvent);
	}

	@Override
	public void deleteEvent(Long houseId, Long eventId, Long actingUserId) {
		Event event = eventServiceHelper.getEventOrThrow(eventId);
		eventPolicy.validateEventBelogsToHouse(event, houseId);
		
		House house = event.getHouse();
		eventPolicy.validateEventOperation(house, actingUserId);
		
		eventRepository.delete(event);
		
	}
}
