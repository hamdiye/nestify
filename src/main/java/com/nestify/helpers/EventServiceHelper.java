package com.nestify.helpers;

import org.springframework.stereotype.Component;

import com.nestify.dataAccess.EventRepository;
import com.nestify.entities.Event;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class EventServiceHelper {
	private EventRepository eventRepository; 
	
	public Event getEventOrThrow(Long eventId) {
		return eventRepository.findById(eventId)
									  .orElseThrow(() -> new RuntimeException("Event bulunamadı!"));
	}
}
