package com.nestify.api;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.business.HouseNeedService;
import com.nestify.core.UserPrincipal;
import com.nestify.dataTransferObject.request.SaveHouseNeedRequestDto;
import com.nestify.dataTransferObject.request.UpdateHouseNeedRequestDto;
import com.nestify.dataTransferObject.response.GetHouseNeedByIdResponseDto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/houses")
@AllArgsConstructor
public class HouseNeedController {
	private HouseNeedService houseNeedService;

	@PostMapping("/{houseId}/house-needs")
	public GetHouseNeedByIdResponseDto addHouseNeed(@PathVariable Long houseId,
			@Valid @RequestBody SaveHouseNeedRequestDto houseNeedRequest) {
		return houseNeedService.addHouseNeed(houseId, houseNeedRequest);
	}

	@PutMapping("/{houseId}/house-needs/{houseNeedId}")
	public GetHouseNeedByIdResponseDto updateHouseNeed(@PathVariable Long houseNeedId, 
			@PathVariable Long houseId, 
			@AuthenticationPrincipal UserPrincipal currentUser,
			@Valid @RequestBody UpdateHouseNeedRequestDto houseNeedRequest) {
		return houseNeedService.updateHouseNeed(houseNeedId, houseId, currentUser.getId(), houseNeedRequest);
	}

	@DeleteMapping("/{houseId}/house-needs/{houseNeedId}")
	public void deleteHouseNeed(@PathVariable Long houseNeedId,
			@PathVariable Long houseId,
			@AuthenticationPrincipal UserPrincipal currentUser) {
		houseNeedService.deleteHouseNeed(houseNeedId, houseId, currentUser.getId());
	}
	
	@GetMapping("/{houseId}/house-needs")
	public List<GetHouseNeedByIdResponseDto> getHouseNeedsFromHouse(@PathVariable Long houseId,
			@AuthenticationPrincipal UserPrincipal currentUser) {
		return houseNeedService.getHouseNeedsFromHouse(houseId, currentUser.getId());
	}
}
