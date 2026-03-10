package com.ats.user.infrastructure.out.mapper;

import com.ats.user.domain.model.User;
import com.ats.user.infrastructure.out.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-10T10:37:40-0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.0.jar, environment: Java 21.0.6 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserEntity toEntity(User user) {
        if ( user == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.id( user.getId() );
        userEntity.name( user.getName() );
        userEntity.lastName( user.getLastName() );
        userEntity.email( user.getEmail() );
        userEntity.countryCode( user.getCountryCode() );
        userEntity.phone( user.getPhone() );
        userEntity.passwordHash( user.getPasswordHash() );
        userEntity.createdAt( user.getCreatedAt() );
        userEntity.updatedAt( user.getUpdatedAt() );
        userEntity.createdBy( user.getCreatedBy() );
        userEntity.updatedBy( user.getUpdatedBy() );
        userEntity.active( user.getActive() );

        userEntity.roles( toRoleEntities(user.getRoles()) );

        return userEntity.build();
    }

    @Override
    public User toDomain(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( userEntity.getId() );
        user.name( userEntity.getName() );
        user.lastName( userEntity.getLastName() );
        user.email( userEntity.getEmail() );
        user.phone( userEntity.getPhone() );
        user.countryCode( userEntity.getCountryCode() );
        user.passwordHash( userEntity.getPasswordHash() );
        user.createdAt( userEntity.getCreatedAt() );
        user.updatedAt( userEntity.getUpdatedAt() );
        user.active( userEntity.getActive() );
        user.createdBy( userEntity.getCreatedBy() );
        user.updatedBy( userEntity.getUpdatedBy() );

        user.roles( toDomainRoles(userEntity.getRoles()) );

        return user.build();
    }
}
