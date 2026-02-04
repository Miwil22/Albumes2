package org.example.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotBlank(message = "El username no puede estar vacío")
    private String username;

    @NotBlank(message = "El password no puede estar vacío")
    @Length(min = 5, message = "El password debe tener al menos 5 caracteres")
    private String password;

    private String role;
}
