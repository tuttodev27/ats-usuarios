package com.ats.user.infrastructure.in.web.mapper;

import com.ats.user.domain.model.User;
import com.ats.user.infrastructure.in.web.dto.request.UpdateUserRequest;
import com.ats.user.infrastructure.in.web.dto.request.UserRequest;
import com.ats.user.infrastructure.in.web.dto.response.UserResponse;
import java.time.LocalDateTime;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-09T09:09:09-0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.0.jar, environment: Java 21.0.9 (Azul Systems, Inc.)"
)
@Component
public class UserWebMapperImpl implements UserWebMapper {

    @Override
    public User toDomain(UserRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.name( request.name() );
        user.lastName( request.lastName() );
        user.email( request.email() );
        user.phone( request.phone() );
        user.countryCode( request.countryCode() );

        user.createdAt( LocalDateTime.now() );
        user.updatedAt( LocalDateTime.now() );
        user.active( true );

        return user.build();
    }

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        LocalDateTime createdAt = null;
        Long id = null;
        String name = null;
        String lastName = null;
        String email = null;
        String countryCode = null;
        String phone = null;

        createdAt = user.getCreatedAt();
        id = user.getId();
        name = user.getName();
        lastName = user.getLastName();
        email = user.getEmail();
        countryCode = user.getCountryCode();
        phone = user.getPhone();

        Set<String> roles = mapRoleNames(user);

        UserResponse userResponse = new UserResponse( id, name, lastName, email, countryCode, phone, createdAt, roles );

        return userResponse;
    }

    @Override
    public User toDomain(UpdateUserRequest update) {
        if ( update == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.name( update.name() );
        user.lastName( update.lastName() );
        user.phone( update.phone() );
        user.countryCode( update.countryCode() );
        user.active( update.active() );

        user.updatedAt( LocalDateTime.now() );

        return user.build();
    }
}
