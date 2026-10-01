package com.nestify.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.nestify.dataTransferObject.response.GetHouseByIdResponseDto;
import com.nestify.dataTransferObject.response.HouseMemberResponseDto;
import com.nestify.entities.House;
import com.nestify.entities.HouseMember;

@Component
public class HouseMapper {

    public GetHouseByIdResponseDto toGetHouseByIdResponseDto(House house) {
        if (house == null) {
            return null;
        }

        Set<HouseMemberResponseDto> memberDtos = house.getMembers().stream()
                .map(this::toUserSummaryForHouseDto)
                .collect(Collectors.toSet());

        return new GetHouseByIdResponseDto(
                house.getId(),
                house.getTitle(),
                house.getAddress(),
                house.getCity(),
                house.getInviteCode(),
                house.getCreatedAt(),
                house.getUpdatedAt(),
                memberDtos);
    }

    private HouseMemberResponseDto toUserSummaryForHouseDto(HouseMember member) {
        return new HouseMemberResponseDto(
                member.getUser().getId(),
                member.getUser().getName(),
                member.getUser().getEmail(),
                member.getJoinedAt(),
                member.getMemberRole());
    }

}
