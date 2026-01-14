package com.itcotato.naengjango.global.config;

import jakarta.servlet.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Profile("local")
public class MockUserIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        System.out.println("### MockUserIdFilter invoked ###");
        request.setAttribute("memberId", 1L);
        chain.doFilter(request, response);
    }
}
