package com.disneymovie.disneyJava.user.session;

import com.disneymovie.disneyJava.user.model.User;
import com.disneymovie.disneyJava.user.model.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.UUID;


public class SessionManagerTest {
    AutoCloseable openMocks;
    SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        //initMocks(this);
        openMocks = MockitoAnnotations.openMocks(this);

        sessionManager = new SessionManager();
    }

    @Test
    void createSession() {
        User user = new User();
        String token = sessionManager.createSession(user);
        sessionManager.sessionMap.put(token, new Session(token, user, new Date(System.currentTimeMillis())));
    }

    @Test
    void getSession() {
        String token = UUID.randomUUID().toString();
        User user = new User();
        Session aux = new Session(token, user, new Date(System.currentTimeMillis()));
        sessionManager.sessionMap.put(token, aux);
        Session aux2 = sessionManager.getSession(token);

        Assertions.assertNotNull(aux2);
        Assertions.assertEquals(aux, aux2);

    }

    @Test
    void getSessionBad() {
        String token = null;
        Session session = sessionManager.getSession(token);

        Assertions.assertEquals(session, token);
    }

    @Test
    void removeSession() {
        String token = UUID.randomUUID().toString();
        sessionManager.sessionMap.remove(token);

        sessionManager.removeSession(token);
    }

    @Test
    void expireSessions() {

    }

    @Test
    void getCurrentUser() {
        String token = UUID.randomUUID().toString();
        User user = new User(1, UserRole.admin,"email","123");
        Session aux = new Session(token, user, new Date(System.currentTimeMillis()));
        sessionManager.sessionMap.put(token, aux);

        User user2 = sessionManager.getCurrentUser(token);

        Assertions.assertNotNull(user2);
        Assertions.assertEquals(user, user2);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}
