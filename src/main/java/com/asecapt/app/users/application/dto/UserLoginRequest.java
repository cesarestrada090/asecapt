package com.asecapt.app.users.application.dto;

import com.asecapt.app.users.domain.entities.User;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public class UserLoginRequest implements Serializable {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserLoginRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}