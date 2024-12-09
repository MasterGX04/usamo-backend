package com.competitionmathacademy.mathapi.config;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityHeaderFilter implements Filter {
    
    @Override
    public void doFilter(
            jakarta.servlet.ServletRequest request,
            jakarta.servlet.ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Cross-Origin-Opener-Policy", "same-origin"); // or "unsafe-none" if needed
        httpResponse.setHeader("Cross-Origin-Embedder-Policy", "require-corp"); // or "unsafe-none" if needed
        chain.doFilter(request, response);
    }
}
