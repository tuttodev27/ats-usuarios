package com.ats.user.infrastructure.adapter.out.persistence.mapper;

import com.ats.user.domain.model.Menu;
import com.ats.user.infrastructure.adapter.out.persistence.entity.MenuEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    @Mapping(target = "moduleId", source = "entity.module.id")
    Menu toDomain(MenuEntity entity);
}
