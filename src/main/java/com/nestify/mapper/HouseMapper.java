package com.nestify.mapper;

import org.springframework.stereotype.Component;

import com.nestify.dataTransferObject.response.GetAllHouseResponseDto;
import com.nestify.dataTransferObject.response.GetHouseByIdResponseDto;
import com.nestify.entities.House;

@Component
public class HouseMapper {

    public GetHouseByIdResponseDto toGetHouseByIdResponseDto(House house) {
        if (house == null) {
            return null;
        }
        return new GetHouseByIdResponseDto(
                house.getId(),
                house.getTitle(),
                house.getAddress(),
                house.getCity(),
                house.getInviteCode(),
                house.getCreatedAt(),
                house.getUpdatedAt(),
                house.getMembers());
    }

    public GetAllHouseResponseDto toGetAllHouseResponseDto(House house) {
        if (house == null) {
            return null;
        }
        return new GetAllHouseResponseDto(
                house.getId(),
                house.getTitle(),
                house.getAddress(),
                house.getCity(),
                house.getInviteCode(),
                house.getCreatedAt(),
                house.getUpdatedAt(),
                house.getMembers());
    }

}
