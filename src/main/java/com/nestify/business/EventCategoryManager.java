package com.nestify.business;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nestify.dataAccess.EventCategoryRepository;
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
	@Transactional
	public List<GetEventCategoryResponseDto> getAllEventCategoryFromHouse(Long houseId, Long actingUserId) {
		House house = houseHelper.getHouseOrThrow(houseId);
		eventCategoryPolicy.validateEventCategoryOperation(house, actingUserId);

		List<EventCategory> categories = eventCategoryRepository.findByHouseId(houseId);

		if (categories.isEmpty()) {
			EventCategory defaultCat = eventCategoryHelper.getOrCreateDefaultCategory(house);
			categories = List.of(defaultCat);
		}

		List<GetEventCategoryResponseDto> eventCategories = categories.stream()
														              .map(eventCategoryMapper::toGetEventCategoryResponseDto)
														              .toList();
		return eventCategories;
	}

	@Override
	public GetEventCategoryResponseDto addEventCategory(Long houseId, Long actingUserId, SaveEventCategoryRequestDto eventCategoryDto) {
		House house = houseHelper.getHouseOrThrow(houseId);
		eventCategoryPolicy.validateEventCategoryOperation(house, eventCategoryDto.getUserId());
		eventCategoryPolicy.validateEventCategoryOperation(house, actingUserId);
		
		EventCategory eventCategory = new EventCategory();
		eventCategory.setTitle(eventCategoryDto.getTitle());
		eventCategory.setDescription(eventCategoryDto.getDescription());
		eventCategory.setColorCode(eventCategoryDto.getColorCode());
		eventCategory.setHouse(house);

		EventCategory savedEventCategory = eventCategoryRepository.save(eventCategory);

		return eventCategoryMapper.toGetEventCategoryResponseDto(savedEventCategory);
	}

	@Override
	public GetEventCategoryResponseDto updateEventCategory(Long houseId, Long eventCategoryId, Long actingUserId, UpdateEventCategoryRequestDto updateCategoryDto) {
		EventCategory eventCategory = eventCategoryHelper
				.getEventCategoryOrThrow(eventCategoryId);
		eventCategoryPolicy.validateEventCategoryBelogsToHouse(eventCategory, houseId);

		House house = houseHelper.getHouseOrThrow(houseId);
		eventCategoryPolicy.validateEventCategoryOperation(house, actingUserId);
		eventCategoryPolicy.validateEventCategoryOperation(house, updateCategoryDto.getUserId());


		eventCategory.setTitle(updateCategoryDto.getTitle());
		eventCategory.setDescription(updateCategoryDto.getDescription());
		eventCategory.setColorCode(updateCategoryDto.getColorCode());
		eventCategory.setHouse(house);

		EventCategory savedEventCategory = eventCategoryRepository.save(eventCategory);

		return eventCategoryMapper.toGetEventCategoryResponseDto(savedEventCategory);
	}

	@Override
	@Transactional
	public void deleteEventCategory(Long houseId, Long eventCategoryId, Long actingUserId) {
		EventCategory eventCategory = eventCategoryHelper
				.getEventCategoryOrThrow(eventCategoryId);
		eventCategoryPolicy.validateEventCategoryBelogsToHouse(eventCategory, houseId);
		
		House house = houseHelper.getHouseOrThrow(houseId);
		eventCategoryPolicy.validateEventCategoryOperation(house, actingUserId);
		
		eventCategoryRepository.delete(eventCategory);
	}

}
