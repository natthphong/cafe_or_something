package com.backendcafe.backend.service;

import com.backendcafe.backend.config.CustomerUserDetailsService;
import com.backendcafe.backend.config.JwtUtil;
import com.backendcafe.backend.constents.CafeConstants;
import com.backendcafe.backend.entity.User;
import com.backendcafe.backend.models.LoginModel;
import com.backendcafe.backend.models.SignupModel;
import com.backendcafe.backend.repository.UserRepository;
import com.backendcafe.backend.untils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class UserService {

    final UserRepository userRepository;
    final CustomerUserDetailsService customerUserDetailsService;
    final AuthenticationManager authenticationManager;
    final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, CustomerUserDetailsService customerUserDetailsService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.customerUserDetailsService = customerUserDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    private boolean validateSignUpMap(SignupModel body) {
        if (body.getName().length() > 0 && body.getContactNumber().length() > 0
                && body.getEmail().length() > 0 && body.getPassword().length() > 0) {
            return true;
        }
        return false;
    }

    private User getUserFromMap(SignupModel body) {
        User user = new User();
        user.setName(body.getName());
        user.setEmail(body.getEmail());
        user.setPassword(body.getPassword());
        user.setContactNumber(body.getContactNumber());
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

    public ResponseEntity<String> signUp(SignupModel body) {
        log.info("signup {} ", body);
        try {
            if (validateSignUpMap(body)) {
                User user = userRepository.findByEmailId(body.getEmail());
                if (Objects.isNull(user)) {
                    userRepository.save(getUserFromMap(body));
                    return CafeUtils.getResponseEntity(CafeConstants.REGISTER_OK, HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.DUPLICATE_EMAIL, HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity(CafeConstants.DEFAULT_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<String> login(LoginModel body) {
        log.info("Inside login {}" , body);
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            body.getEmail(), body.getPassword()
                    )
            );
            if (auth.isAuthenticated()) {
                if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(
                                    customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getRole()) + "\"}", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("{\"message \":\"" + "Wait for admin approval."
                            + "\"}", HttpStatus.OK);
                }
            }
        } catch (Exception ex) {
            log.error("message {}" ,ex);
        }
        return CafeUtils.getResponseEntity("{\"message \":\"" + "Bad Credntials."
                + "\"}", HttpStatus.BAD_REQUEST);

    }


}
