package com.nestify.dataTransferObject.request;

import com.nestify.entities.enums.MemberRole;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddUserToHouseRequestDto {
	@NotNull(message = "Kullanıcı ID'si belirtilmeli")
	private Long userId;
	@NotNull(message = "Üye rolü belirtilmeli")
	private MemberRole memberRole;
}
