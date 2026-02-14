package com.ats.user.domain.model;

import lombok.*;

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
