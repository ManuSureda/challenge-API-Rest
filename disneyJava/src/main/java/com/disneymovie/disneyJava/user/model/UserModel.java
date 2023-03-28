package com.disneymovie.disneyJava.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private int idUser;
    @Column(name = "user_role")
    private UserRole userRole;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
}
