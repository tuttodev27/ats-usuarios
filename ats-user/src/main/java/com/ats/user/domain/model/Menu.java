package com.ats.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;

import lombok.NoArgsConstructor;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
    public class Menu {
        Long id;
        String title;
        String path;
        Long moduleId;
        Integer orderIndex;
        String requiredPermissionCode;
        boolean active;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }



