package com.asecapt.app.users.domain.services;

import com.asecapt.app.users.application.dto.LoginResponseDto;
import com.asecapt.app.users.application.dto.ResultPage;
import com.asecapt.app.users.application.dto.UserDto;
import com.asecapt.app.users.application.dto.UserResponseDto;
import com.asecapt.app.users.domain.entities.User;
import com.asecapt.app.users.application.dto.UserLoginRequest;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto save(UserDto userDto);
    UserDto update(Integer id, UserDto dto);
    UserResponseDto getByUsernameAndPassword(UserLoginRequest loginRequest);
    Boolean usernameAlreadyExistsByOrgId(String username, Integer orgId);
    Boolean emailAlreadyExists(String email);
    UserResponseDto getById(Integer id);
    User getUserEntityById(Integer id);
    User getUserEntityByUsername(String username);
    ResultPage<UserResponseDto> getAll(Pageable paging);
    UserResponseDto verifyEmail(String token) throws Exception;
    LoginResponseDto login(String username, String password);
    UserResponseDto upgradeToPremium(Integer userId, String planType);
    void saveUserEntity(User user);
}
