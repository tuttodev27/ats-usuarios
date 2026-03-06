package com.ats.user.support;

import com.ats.user.domain.model.Role;
import com.ats.user.domain.model.User;
import com.ats.user.infrastructure.out.entity.RoleEntity;
import com.ats.user.infrastructure.out.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public final class UserDumpData {

    private UserDumpData() {
    }

    public static User domainUserCreate() {
        return User.builder()
                .id(3L)
                .name("Pablo")
                .lastName("Gallegos")
                .email("pgallegoscelis86@gmail.com")
                .countryCode("+56")
                .phone("989421155")
                .passwordHash("$2a$10$seedHash")
                .active(null)
                .roles(new HashSet<>(Set.of(domainRole("RECRUITER"))))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static User domainUserExisting() {
        return User.builder()
                .id(1L)
                .name("System")
                .lastName("Admin")
                .email("admin@ats.local")
                .countryCode("+57")
                .phone("3001002000")
                .passwordHash("$2y$10$mwMJ3Kx6zImKQqs5/EfuiOsJsaG6H6/lU/cSUrOP/aXZekNX34z6m")
                .active(true)
                .roles(new HashSet<>(Set.of(domainRole("ADMIN"))))
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    public static User domainUpdateInput() {
        return User.builder()
                .name("Pablo Updated")
                .lastName("Gallegos Updated")
                .countryCode("+51")
                .phone("987123123")
                .active(false)
                .build();
    }

    public static Role domainRole(String roleName) {
        return Role.builder()
                .name(roleName)
                .active(true)
                .build();
    }

    public static UserEntity entityUserForAuth(boolean active, Set<RoleEntity> roles) {
        return UserEntity.builder()
                .id(1L)
                .name("System")
                .lastName("Admin")
                .email("admin@ats.local")
                .countryCode("+57")
                .phone("3001002000")
                .passwordHash("$2y$10$hash")
                .active(active)
                .roles(roles)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    public static RoleEntity entityRole(String name, boolean active) {
        return RoleEntity.builder()
                .id((long) Math.abs(name.hashCode()))
                .name(name)
                .active(active)
                .description("dump role")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
    }
}
