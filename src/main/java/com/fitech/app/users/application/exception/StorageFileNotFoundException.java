package com.fitech.app.users.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class StorageFileNotFoundException extends RuntimeException {

  public StorageFileNotFoundException(String message) {
    super(message);
  }

  public StorageFileNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
