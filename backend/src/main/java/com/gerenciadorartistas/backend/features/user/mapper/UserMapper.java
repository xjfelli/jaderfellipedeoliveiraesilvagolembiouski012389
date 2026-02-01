package com.gerenciadorartistas.backend.features.user.mapper;

import com.gerenciadorartistas.backend.features.user.dto.UserPresenterDTO;
import com.gerenciadorartistas.backend.features.user.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserPresenterDTO toDto(UserEntity user);
}
