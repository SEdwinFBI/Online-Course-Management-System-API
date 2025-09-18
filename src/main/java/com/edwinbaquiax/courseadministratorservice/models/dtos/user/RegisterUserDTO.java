package com.edwinbaquiax.courseadministratorservice.models.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterUserDTO {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 5, max = 50)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "La contraseña debe tener al menos un número, una mayúscula y un carácter especial")
    private String password;
    @NotBlank
    @Size(min = 3, max = 20)
    private String name;
    private String lastname;
}
