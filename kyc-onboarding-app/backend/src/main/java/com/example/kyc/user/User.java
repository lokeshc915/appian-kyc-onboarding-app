package com.example.kyc.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "app_user")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(nullable = false)
  private String passwordHash;

  @Column(unique = true)
  private String email;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name="user_roles", joinColumns=@JoinColumn(name="user_id"))
  @Enumerated(EnumType.STRING)
  private Set<Role> roles = new HashSet<>();
}
