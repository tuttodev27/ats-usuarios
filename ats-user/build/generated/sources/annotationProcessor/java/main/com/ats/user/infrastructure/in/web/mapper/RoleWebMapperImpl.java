package com.ats.user.infrastructure.in.web.mapper;

import com.ats.user.domain.model.Role;
import com.ats.user.infrastructure.in.web.dto.request.CreateRoleRequest;
import com.ats.user.infrastructure.in.web.dto.request.UpdateRoleRequest;
import com.ats.user.infrastructure.in.web.dto.response.RoleResponse;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-08T16:23:08-0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.0.jar, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class RoleWebMapperImpl implements RoleWebMapper {

    @Override
    public Role toDomain(CreateRoleRequest request) {
        if ( request == null ) {
            return null;
        }

        Role.RoleBuilder role = Role.builder();

        role.name( request.name() );
        role.description( request.description() );
        role.active( request.active() );

        return role.build();
    }

    @Override
    public Role toDomain(UpdateRoleRequest request) {
        if ( request == null ) {
            return null;
        }

        Role.RoleBuilder role = Role.builder();

        role.name( request.name() );
        role.description( request.description() );
        role.active( request.active() );

        return role.build();
    }

    @Override
    public RoleResponse toResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        Long id = null;
        String name = null;
        String description = null;
        boolean active = false;
        LocalDateTime createdAt = null;
        LocalDateTime updatedAt = null;

        id = role.getId();
        name = role.getName();
        description = role.getDescription();
        active = role.isActive();
        createdAt = role.getCreatedAt();
        updatedAt = role.getUpdatedAt();

        RoleResponse roleResponse = new RoleResponse( id, name, description, active, createdAt, updatedAt );

        return roleResponse;
    }
}
