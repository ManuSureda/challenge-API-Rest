package com.disneymovie.disneyJava.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.platform.commons.util.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    String email;
    String password;

    @JsonIgnore
    public Boolean isValid() {
        return !StringUtils.isBlank(email) && !StringUtils.isBlank(password);
    }
}
