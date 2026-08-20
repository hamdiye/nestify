package com.nestify.business;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nestify.dataAccess.EventCategoryRepository;
import com.nestify.dataTransferObject.request.DeleteEventCategoryRequestDto;
import com.nestify.dataTransferObject.request.SaveEventCategoryRequestDto;
import com.nestify.dataTransferObject.request.UpdateEventCategoryRequestDto;
import com.nestify.dataTransferObject.response.GetEventCategoryResponseDto;
import com.nestify.entities.EventCategory;
import com.nestify.entities.House;
import com.nestify.helpers.EventCategoryServiceHelper;
import com.nestify.helpers.HouseServiceHelper;
import com.nestify.mapper.EventCategoryMapper;
import com.nestify.policies.EventCategoryPolicy;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EventCategoryManager implements EventCategoryService {
	private final EventCategoryRepository eventCategoryRepository;
	private final EventCategoryPolicy eventCategoryPolicy;
	private final HouseServiceHelper houseHelper;
	private final EventCategoryMapper eventCategoryMapper;
	private final EventCategoryServiceHelper eventCategoryHelper;

	@Override
	public List<GetEventCategoryResponseDto> getAllEventCategoryFromHouse(Long houseId, Long actingUserId) {
		House house = houseHelper.getHouseOrThrow(houseId);
		eventCategoryPolicy.validateEventCategory(house, actingUserId);
		List<GetEventCategoryResponseDto> eventCategories = house.getEventCategories()
				.stream()
				.map(eventCategory -> eventCategoryMapper.toGetEventCategoryResponseDto(eventCategory))
				.toList();
		return eventCategories;
	}

	@Override
	public GetEventCategoryResponseDto addEventCategory(SaveEventCategoryRequestDto eventCategoryDto) {
		House house = houseHelper.getHouseOrThrow(eventCategoryDto.getHouseId());

		eventCategoryPolicy.validateEventCategory(house, eventCategoryDto.getUserId());

		EventCategory eventCategory = new EventCategory();
		eventCategory.setTitle(eventCategoryDto.getTitle());
		eventCategory.setDescription(eventCategoryDto.getDescription());
		eventCategory.setColorCode(eventCategoryDto.getColorCode());
		eventCategory.setHouse(house);

		EventCategory savedEventCategory = eventCategoryRepository.save(eventCategory);

		return eventCategoryMapper.toGetEventCategoryResponseDto(savedEventCategory);
	}

	@Override
	public GetEventCategoryResponseDto updateEventCategory(UpdateEventCategoryRequestDto updateCategoryDto) {
		House house = houseHelper.getHouseOrThrow(updateCategoryDto.getHouseId());
		EventCategory eventCategory = eventCategoryHelper
				.getEventCategoryOrThrow(updateCategoryDto.getEventCategoryId());

		eventCategoryPolicy.validateEventCategory(house, updateCategoryDto.getUserId());

		eventCategory.setTitle(updateCategoryDto.getTitle());
		eventCategory.setDescription(updateCategoryDto.getDescription());
		eventCategory.setColorCode(updateCategoryDto.getColorCode());
		eventCategory.setHouse(house);

		EventCategory savedEventCategory = eventCategoryRepository.save(eventCategory);

		return eventCategoryMapper.toGetEventCategoryResponseDto(savedEventCategory);
	}

	@Override
	public void deleteEventCategory(DeleteEventCategoryRequestDto deleteEventDto) {
		House house = houseHelper.getHouseOrThrow(deleteEventDto.getHouseId());
		eventCategoryPolicy.validateEventCategory(house, deleteEventDto.getActingUserId());
		EventCategory eventCategory = eventCategoryHelper.getEventCategoryOrThrow(deleteEventDto.getCategoryId());
		eventCategoryRepository.delete(eventCategory);
	}

}
