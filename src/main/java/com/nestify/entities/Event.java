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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name= "title", nullable = false)
	private String title;
	
	@Column(name= "description")
	private String description;
	
	@Column(name= "start_date_time", nullable = false)
	private LocalDateTime startDateTime;
	
	@Column(name= "end_date_time")
	private LocalDateTime endDateTime;
	
	@Column(name= "all_day", nullable = false)
	private boolean allDay = false;
	
	@Column(name= "location")
	private String location;
	
	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name= "event_category_id", nullable = false)
	private EventCategory eventCategory;
	
	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name= "house_id", nullable = false)
	private House house;
	
	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name= "assigned_user_id")
	private User assignedUser;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
