package com.disneymovie.disneyJava.user.session;

import com.disneymovie.disneyJava.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    String token;
    User loggedUser;
    Date lastAction;
}
