package com.nestify.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.business.HouseNeedService;
import com.nestify.dataTransferObject.request.DeleteHouseNeedRequestDto;
import com.nestify.dataTransferObject.request.SaveHouseNeedRequestDto;
import com.nestify.dataTransferObject.request.UpdateHouseNeedRequestDto;
import com.nestify.dataTransferObject.response.GetHouseNeedByIdResponseDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/house-needs")
@AllArgsConstructor
public class HouseNeedController {
	private HouseNeedService houseNeedService;
	
	@PostMapping("/add")
	public GetHouseNeedByIdResponseDto addHouseNeed(@Valid @RequestBody SaveHouseNeedRequestDto houseNeedRequest) {
		return houseNeedService.addHouseNeed(houseNeedRequest);
	}
	
	@PostMapping("/update")
	public GetHouseNeedByIdResponseDto updateHouseNeed(@Valid @RequestBody UpdateHouseNeedRequestDto houseNeedRequest) {
		return houseNeedService.updateHouseNeed(houseNeedRequest);
	}
	
	@DeleteMapping("/delete")
	public void deleteHouseNeed(@Valid @RequestBody DeleteHouseNeedRequestDto houseNeedRequest) {
		 houseNeedService.deleteHouseNeed(houseNeedRequest);
	}
}
