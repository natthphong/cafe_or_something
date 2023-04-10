package com.backendcafe.backend.config;

import com.backendcafe.backend.exception.BaseException;
import com.backendcafe.backend.models.JsonModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CustomerUserDetailsService service;

    public JwtFilter(JwtUtil jwtUtil, CustomerUserDetailsService service) {
        this.jwtUtil = jwtUtil;
        this.service = service;
    }

    Claims claims = null;
    private String username = null;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        log.info("VALIDATE TOKEN");
        ///user/reset-password/*|/product/getImage/*


        if (request.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")) {
            filterChain.doFilter(request, response);
        } else {
            String[] str = request.getServletPath().toString().split("/");
            log.info("path {}", str[2]);
            if (str.length > 2 && (str[2].equals("getImage") || str[2].equals("reset-password"))) {
                filterChain.doFilter(request, response);
                return;
            }

            String authorizationHeader = request.getHeader("Authorization");
            String token = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

                try {
                    token = authorizationHeader.substring(7);

                    username = jwtUtil.extractUsername(token);
                    claims = jwtUtil.extractAllClaims(token);

                } catch (Exception ex) {
                    log.info("HELLO ERROR");
                }

            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("CLAIMS USER");
                UserDetails userDetails = service.loadUserByUsername(username);
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            } else {
                log.info("TOKEN INVALID");
                JsonModel jsonModel = new JsonModel();
                jsonModel.setMessage("TOKEN INVALID");
                jsonModel.setStatus(String.valueOf(HttpStatus.FORBIDDEN));
                String strJson = new Gson().toJson(jsonModel);

                response.setHeader("Content-Type", "application/json");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                PrintWriter out = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                out.print(strJson);
                out.flush();
                return;

            }


            filterChain.doFilter(request, response);
        }


    }

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase((String) claims.get("role"));
    }

    public boolean isUser() {
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }

    public String getCurrentUser() {
        return username;
    }

}
