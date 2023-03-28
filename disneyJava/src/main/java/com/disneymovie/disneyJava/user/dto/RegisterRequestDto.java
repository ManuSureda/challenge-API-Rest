package com.disneymovie.disneyJava.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.platform.commons.util.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {
    Integer userRoleId;
    String email;
    String password;

    @JsonIgnore
    public boolean isValid() {
        return userRoleId > 0 && userRoleId < 3 &&
                !StringUtils.isBlank(email) &&
                !StringUtils.isBlank(password);
    }
}
