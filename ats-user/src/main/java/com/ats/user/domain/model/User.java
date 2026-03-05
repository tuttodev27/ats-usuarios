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
@Getter
@Setter
@Builder
@ToString(exclude = "roles")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @EqualsAndHashCode.Include
    Long id;
    String name;
    String lastName;
    String email;
    String phone;
    String countryCode;
    String passwordHash;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Boolean active;
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
