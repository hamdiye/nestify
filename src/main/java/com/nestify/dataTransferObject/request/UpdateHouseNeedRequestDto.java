package com.nestify.dataTransferObject.request;

import com.nestify.entities.enums.NeedStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHouseNeedRequestDto {
	@NotBlank(message = "Başlık boş olamaz")
	@Size(max = 100, message = "Başlık en fazla 100 karakter olabilir")
	private String title;
	private String description;
	@NotNull(message = "Durum belirtilmeli")
	private NeedStatus status;
}
