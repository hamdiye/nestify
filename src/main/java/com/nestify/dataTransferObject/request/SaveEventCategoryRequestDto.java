package com.nestify.dataTransferObject.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveEventCategoryRequestDto {
	private Long userId;
	private String title;
	private String description;
	
	@NotBlank(message = "Renk kodu boş bırakılamaz.")
	@Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Geçerli bir HEX renk kodu giriniz (Örn: #FF5733).")
	private String colorCode;
}
