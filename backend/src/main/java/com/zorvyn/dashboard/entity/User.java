package com.zorvyn.dashboard.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.zorvyn.dashboard.enums.Role;
import com.zorvyn.dashboard.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter   /*using lombok annotation so don't need getter and setter which makes code lengthy */
@Setter
@EntityListeners(AuditingEntityListener.class)  /* automatically fills fields of created_at and updated_at whenever entity will change */
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) /* generation type is UUID and not Identity which is integer increment type , uuid is more secure and flexible , no merge conflict*/
    @Column(name = "id", updatable = false, nullable = false, length = 35)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable =false)
    private Role role = Role.VIEWER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
