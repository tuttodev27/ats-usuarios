package com.ats.user.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = "roles")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @EqualsAndHashCode.Include
    Long id;

    String name;
    String lastName;
    String email;
    String phone;
    String indicativo;
    String passwordHash;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Long createdBy;
    Long updatedBy;

    Set<Role> roles = new HashSet<>();

    public void addRole(Role r){
        roles.add(r);
    }

    public void removeRole(Role r) {
        roles.remove(r);
    }
}
