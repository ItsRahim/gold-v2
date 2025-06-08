package com.rahim.authenticationservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", schema = "authentication-service")
public class User {
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 50)
    @NotNull
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Size(max = 100)
    @NotNull
    @Email
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @NotNull
    @Column(name = "password", nullable = false, length = Integer.MAX_VALUE)
    private String password;

    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @Size(max = 20)
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @ColumnDefault("false")
    @Column(name = "enabled")
    private Boolean enabled;

    @ColumnDefault("false")
    @Column(name = "email_verified")
    private Boolean emailVerified;

    @ColumnDefault("false")
    @Column(name = "phone_verified")
    private Boolean phoneVerified;

    @Size(max = 10)
    @Column(name = "locale", length = 10)
    private String locale;

    @Size(max = 50)
    @Column(name = "timezone", length = 50)
    private String timezone;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "last_login")
    private OffsetDateTime lastLogin;

    @Column(name = "password_change_at")
    private OffsetDateTime passwordChangeAt;

}