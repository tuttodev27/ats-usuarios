package com.ats.user.infrastructure.out.adapter;

import com.ats.user.domain.model.Permission;
import com.ats.user.infrastructure.out.entity.PermissionEntity;
import com.ats.user.infrastructure.out.entity.RoleEntity;
import com.ats.user.infrastructure.out.entity.RolePermissionEntity;
import com.ats.user.infrastructure.out.entity.RolePermissionId;
import com.ats.user.infrastructure.out.repository.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RolePermissionSyncSupport {

    private final PermissionJpaRepository permissionJpaRepository;

    public void sync(RoleEntity entity, Set<Permission> requestedPermissions) {
        Set<Long> permissionIds = requestedPermissions == null
                ? Set.of()
                : requestedPermissions.stream()
                .map(Permission::getId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<Long, RolePermissionEntity> existingByPermissionId = entity.getRolePermissions().stream()
                .collect(Collectors.toMap(
                        rolePermission -> rolePermission.getPermission().getId(),
                        Function.identity(),
                        (left, right) -> left
                ));

        LocalDateTime now = LocalDateTime.now();
        existingByPermissionId.forEach((permissionId, relation) -> {
            relation.setActive(permissionIds.contains(permissionId));
            relation.setUpdatedAt(now);
        });

        Set<Long> missingPermissionIds = new HashSet<>(permissionIds);
        missingPermissionIds.removeAll(existingByPermissionId.keySet());

        if (missingPermissionIds.isEmpty()) {
            return;
        }

        Map<Long, PermissionEntity> permissionsById = permissionJpaRepository.findAllById(missingPermissionIds).stream()
                .collect(Collectors.toMap(PermissionEntity::getId, Function.identity()));

        missingPermissionIds.forEach(permissionId -> {
            PermissionEntity permission = permissionsById.get(permissionId);
            if (permission == null) {
                return;
            }
            entity.getRolePermissions().add(RolePermissionEntity.builder()
                    .id(new RolePermissionId(entity.getId(), permissionId))
                    .role(entity)
                    .permission(permission)
                    .active(true)
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        });
    }
}
