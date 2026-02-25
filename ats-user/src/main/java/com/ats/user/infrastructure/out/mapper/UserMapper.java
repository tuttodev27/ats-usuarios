package com.ats.user.infrastructure.out.mapper;

import com.ats.user.domain.model.User;
import com.ats.user.infrastructure.out.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface UserMapper {
    @Mapping(target="roles",  ignore= true)
    UserEntity toEntity(User user);
    User toDomain(UserEntity userEntity);

}
