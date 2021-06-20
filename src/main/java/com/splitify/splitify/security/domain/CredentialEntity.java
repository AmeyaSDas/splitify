package com.splitify.splitify.security.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CREDENTIAL")
@Builder
public class CredentialEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "CREDENTIALID")
  private int credentialId;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "USERID", nullable = false)
  private UserEntity user;
  @Column(name = "PASSWORD")
  private String password;
}
