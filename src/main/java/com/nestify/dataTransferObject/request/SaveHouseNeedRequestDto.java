package com.nestify.dataTransferObject.request;

import com.nestify.entities.enums.NeedStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveHouseNeedRequestDto {
	private String title;
	private String description;
    private Long createdBy_id;
    private Long house_id;
    private NeedStatus status;
}
