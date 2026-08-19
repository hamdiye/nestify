package com.nestify.dataTransferObject.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDto {
	
	@NotBlank(message = "İsim alanı boş bırakılamaz.")
	@Size(min = 2, max = 50, message = "İsim 2 ile 50 karakter arasında olmalıdır.")
	private String name;
	
	@NotBlank(message = "E-posta alanı boş bırakılamaz.")
    @Email(message = "Lütfen geçerli bir e-posta adresi giriniz.")
	private String email;
	
	@NotBlank(message = "Şifre boş bırakılamaz.")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır.")
	private String password;
	


}
