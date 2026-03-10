package com.ats.user.infrastructure.out.mapper;

import com.ats.user.domain.model.Role;
import com.ats.user.infrastructure.out.entity.RoleEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-10T10:43:57-0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.0.jar, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public Role toDomain(RoleEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Role.RoleBuilder role = Role.builder();

        role.id( entity.getId() );
        role.name( entity.getName() );
        role.description( entity.getDescription() );
        if ( entity.getActive() != null ) {
            role.active( entity.getActive() );
        }
        role.createdAt( entity.getCreatedAt() );
        role.updatedAt( entity.getUpdatedAt() );

        role.permissions( toDomainPermissions(entity.getRolePermissions()) );

        return role.build();
    }
}
