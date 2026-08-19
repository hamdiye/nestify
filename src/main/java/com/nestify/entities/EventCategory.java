package com.nestify.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="event_categories")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCategory {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(name="title", nullable = false)
	private String title;
	@Column(name="description", nullable = true)
	private String description;
	@NotBlank(message = "Renk kodu boş bırakılamaz.")
	@Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Geçerli bir HEX renk kodu giriniz (Örn: #FF5733).")
	@Column(name="color_code",nullable = false, length = 7)
	private String colorCode;
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
	private House house;
	
	
}
