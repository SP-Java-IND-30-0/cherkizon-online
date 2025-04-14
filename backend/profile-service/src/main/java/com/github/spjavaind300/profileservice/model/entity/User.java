package com.github.spjavaind300.profileservice.model.entity;

import jakarta.persistence.*;
import lombok.*;



@Entity
@Table(name = "users", schema = "app")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone")
    private String phone;


    @Column(name = "image")
    private String image;

}