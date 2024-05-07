package com.example.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Setter
@Getter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "role_entity")
public class RoleEntity {
    @Id
    @Column(name = "RoleId")
    private int roleId;

    @Column(name = "RoleName")
    private String roleName;
}