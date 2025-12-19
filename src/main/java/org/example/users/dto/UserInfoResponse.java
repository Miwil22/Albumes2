package org.example.users.dto;

import org.example.users.models.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String nombre;
    private String apellidos;
    private String username;
    private String email;
    private Set<Role> roles;
    private Boolean isDeleted;
    private List<String> albumes;
}