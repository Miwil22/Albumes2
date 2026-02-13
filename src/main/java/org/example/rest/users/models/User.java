package org.example.rest.users.models;

import org.example.rest.artistas.models.Artista;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USUARIOS")
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String nombre;
  @Column(nullable = false)
  private String apellidos;
  @Column(unique = true, nullable = false)
  private String username;
  @Column(unique = true, nullable = false)
  @Email(regexp = ".*@.*\\..*", message = "Email debe ser válido")
  private String email;
  @Length(min = 5, message = "Password debe tener al menos 5 caracteres")
  @Column(nullable = false)
  private String password;
  @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @Builder.Default
  private LocalDateTime createdAt = LocalDateTime.now();
  @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  @Builder.Default
  private LocalDateTime updatedAt = LocalDateTime.now();
  @Column(columnDefinition = "boolean default false")
  @Builder.Default
  private Boolean isDeleted = false;

  @ElementCollection(fetch = FetchType.EAGER)
  @Enumerated(EnumType.STRING)
  private Set<Role> roles;

  @OneToOne
  @JoinColumn(name = "artista_id")
  private Artista artista;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
        .collect(Collectors.toSet());
  }

  @Override
  public String getUsername() {
    // email in our case
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return !isDeleted;
  }
}