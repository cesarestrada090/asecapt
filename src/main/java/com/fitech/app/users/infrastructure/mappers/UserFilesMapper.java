package com.fitech.app.users.infrastructure.mappers;

import com.fitech.app.commons.util.MapperUtil;
import com.fitech.app.users.domain.entities.UserFiles;
import com.fitech.app.users.application.dto.UserFilesDto;

public class UserFilesMapper {

  public static UserFiles toEntity(UserFilesDto dto) {
    return MapperUtil.map(dto, UserFiles.class);
  }

  public static UserFilesDto toDto(UserFiles entity) {
    return MapperUtil.map(entity, UserFilesDto.class);
  }
}
