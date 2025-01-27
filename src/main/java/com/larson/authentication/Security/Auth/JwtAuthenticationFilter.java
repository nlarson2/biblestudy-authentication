package com.larson.authentication.Security.Auth;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.larson.authentication.Security.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // verify that authorization token as been added
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("NO BEARER TOKEN ADDED");
                filterChain.doFilter(request, response);
                return;
            }

            //parse user token and get the user for the client associated with that token
            String jwtToken = authHeader.substring(7);
            Optional<User> user = jwtService.getUserData(jwtToken);
            if(!user.isPresent()) throw new Exception("Failed to find user");

            User authenticatedUser = user.get();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        
        } catch (Exception ex ) {
            ex.printStackTrace();
        }
        
        filterChain.doFilter(request, response);
        return;
    }
    
}
