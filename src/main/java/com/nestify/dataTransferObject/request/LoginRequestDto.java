package com.nestify.dataTransferObject.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequestDto {
	@NotBlank(message = "E-posta adresi boş bırakılamaz.")
    @Email(message = "Geçerli bir e-posta adresi giriniz.")
	private String email;
	@NotBlank(message = "Şifre boş bırakılamaz.")
	private String password;
}
