package com.ats.user.domain.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Module {
    @EqualsAndHashCode.Include
    Long id;

    String code;
    String name;
    String description;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    boolean active;
    Long createdBy;
    Long updatedBy;

    Set<Menu> menus = new HashSet<>();

    public void addMenu(Menu menu) {
        menus.add(menu);
        menu.setModuleId(this.id); // ver Menu abajo
    }
}

