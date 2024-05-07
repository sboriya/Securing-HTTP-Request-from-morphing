package com.example.entity;





import jakarta.persistence.*;
import lombok.Data;



@Data
@Entity
@Table(name = "admin")
public class AdminEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private int id;
    @Column(name = "admin_name" )
    private String userName;
    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(
            name = "admin_role",
            joinColumns = @JoinColumn(name = "admin_id"  ),
            inverseJoinColumns = @JoinColumn(name = "role_id" )
    )
    private RoleEntity roleEntity;

    @Column(name = "is_deleted", columnDefinition = "tinyint(1) default 0" ,nullable = false)
    private boolean isDeleted;


}

