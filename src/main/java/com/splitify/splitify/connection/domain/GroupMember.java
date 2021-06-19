package com.splitify.splitify.connection.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "GROUPMEMBER")
@Builder
public class GroupMember {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  @Column(name = "GROUPMEMBERID")
  private Integer groupMemberId;

  @Column(name = "USERID")
  private Integer userId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "GROUPID", nullable = false)
  private Group group;

  @Column(name = "STATUS")
  private String status;
}
