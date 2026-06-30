package com.ats.user.infrastructure.adapter.in.web.mapper;

import com.ats.user.domain.model.Menu;
import com.ats.user.infrastructure.adapter.in.web.dto.request.CreateMenuRequest;
import com.ats.user.infrastructure.adapter.in.web.dto.request.UpdateMenuRequest;
import com.ats.user.infrastructure.adapter.in.web.dto.response.MenuResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuWebMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Menu toDomain(CreateMenuRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Menu toDomain(UpdateMenuRequest request);

    MenuResponse toResponse(Menu menu);
}
