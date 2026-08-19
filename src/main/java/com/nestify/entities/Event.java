package com.nestify.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(name="title")
	private String title;
	@Column(name="desription")
	private String description;
	@Column(name="started_date")
	private LocalDateTime startedDate;
	@Column(name="end_date")
	private LocalDateTime endDate;
	@Column(nullable = false)
    private Boolean isAllDay = false;
	@Column(name="location")
	private String location;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_category_id")
	private EventCategory eventCategory;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;
}
