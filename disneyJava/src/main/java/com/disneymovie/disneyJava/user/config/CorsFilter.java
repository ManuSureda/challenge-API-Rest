package com.disneymovie.disneyJava.user.config;

import com.disneymovie.disneyJava.user.session.Session;
import com.disneymovie.disneyJava.user.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CorsFilter implements Filter {

    @Autowired
    SessionManager sessionManager;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ( httpRequest.getRequestURI().equals("/auth/login") ) {
            chain.doFilter(request, response);
        } else if (httpRequest.getMethod().equals(HttpMethod.OPTIONS.name())) {
            httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT ,DELETE");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me, authorization");
            chain.doFilter(httpRequest, httpResponse);
        } else {
            String sessionToken = httpRequest.getHeader("authorization");
            Session session = sessionManager.getSession(sessionToken);

            httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
            httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT ,DELETE");
            httpResponse.setHeader("Access-Control-Max-Age", "3600");
            httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me, authorization");

            if (null != session) {
                chain.doFilter(httpRequest, httpResponse);
            } else {
                httpResponse.setStatus(HttpStatus.FORBIDDEN.value());
            }
        }


    }

}
