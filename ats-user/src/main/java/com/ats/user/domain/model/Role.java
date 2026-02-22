package com.ats.user.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = {"permissions"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role {

    @EqualsAndHashCode.Include
    Long id;
    String name;
    String description;
    boolean active;
    Long createdBy;
    Long updatedBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    Set<Permission> permissions = new HashSet<>();

    public void addPermission(Permission p) {
        permissions.add(p);
    }

    public void removePermission(Permission p) {
        permissions.remove(p);
    }
}
