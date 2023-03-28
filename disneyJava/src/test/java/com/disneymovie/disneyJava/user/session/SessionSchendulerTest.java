package com.disneymovie.disneyJava.user.session;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SessionSchendulerTest {
    AutoCloseable openMocks;
    SessionScheduler sessionScheduler;

    @Mock
    SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        //initMocks(this);
        openMocks = MockitoAnnotations.openMocks(this);

        sessionScheduler = new SessionScheduler(sessionManager);
    }

    @Test
    void expiresSessions() {
        sessionScheduler.expiresSessions();
        verify(sessionManager, times(1)).expireSessions();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}
