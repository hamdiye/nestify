package com.nestify.dataTransferObject.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JoinHouseByInviteCodeRequestDto {
	@NotNull(message = "Davet kodu belirtilmeli")
	private String inviteCode;
}
