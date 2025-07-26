package com.asecapt.app.users.infrastructure.mappers;

import com.asecapt.app.commons.util.MapperUtil;
import com.asecapt.app.users.domain.entities.UserFiles;
import com.asecapt.app.users.application.dto.UserFilesDto;

public class UserFilesMapper {

  public static UserFiles toEntity(UserFilesDto dto) {
    return MapperUtil.map(dto, UserFiles.class);
  }

  public static UserFilesDto toDto(UserFiles entity) {
    return MapperUtil.map(entity, UserFilesDto.class);
  }
}
