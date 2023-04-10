package com.backendcafe.backend.service;

import com.backendcafe.backend.config.CustomerUserDetailsService;
import com.backendcafe.backend.config.JwtFilter;
import com.backendcafe.backend.config.JwtUtil;
import com.backendcafe.backend.constents.CafeConstants;
import com.backendcafe.backend.entity.PasswordResetToken;
import com.backendcafe.backend.entity.User;
import com.backendcafe.backend.models.JsonModel;
import com.backendcafe.backend.models.LoginModel;
import com.backendcafe.backend.models.SignupModel;
import com.backendcafe.backend.repository.PasswordResetTokenRepository;
import com.backendcafe.backend.repository.UserRepository;
import com.backendcafe.backend.untils.CafeUtils;
import com.backendcafe.backend.untils.EmailUtils;
import com.backendcafe.backend.wrapper.UserWrapper;
import com.google.common.base.Strings;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserService {

    final private UserRepository userRepository;
    final private CustomerUserDetailsService customerUserDetailsService;
    final private AuthenticationManager authenticationManager;
    final private JwtUtil jwtUtil;
    final private PasswordEncoder bcryptEncoder;
    final private JwtFilter jwtFilter;
    final private EmailUtils emailUtils;

    final private PasswordResetTokenRepository passwordResetTokenRepository;

    public UserService(UserRepository userRepository, CustomerUserDetailsService customerUserDetailsService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, PasswordEncoder bcryptEncoder, JwtFilter jwtFilter, EmailUtils emailUtils, PasswordResetTokenRepository passwordResetTokenRepository) {
        this.userRepository = userRepository;
        this.customerUserDetailsService = customerUserDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.bcryptEncoder = bcryptEncoder;
        this.jwtFilter = jwtFilter;
        this.emailUtils = emailUtils;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
    }

    private boolean validateSignUpMap(SignupModel body) {
        if (!Strings.isNullOrEmpty(body.getName()) && !Strings.isNullOrEmpty(body.getContactNumber())
                && !Strings.isNullOrEmpty(body.getEmail()) && !Strings.isNullOrEmpty(body.getPassword())) {
            return true;
        }
        return false;
    }

    private User getUserFromMap(SignupModel body) {
        User user = new User();
        user.setName(body.getName());
        user.setEmail(body.getEmail());

        user.setPassword(bcryptEncoder.encode(body.getPassword()));
        user.setContactNumber(body.getContactNumber());
        user.setStatus("false");
        user.setRole("user");
        return user;
    }

    public ResponseEntity<JsonModel> signUp(SignupModel body) throws JSONException {
        log.info("signup {} ", body);
        try {
            if (validateSignUpMap(body)) {
                User user = userRepository.findByEmailId(body.getEmail());
                if (Objects.isNull(user)) {
                    userRepository.save(getUserFromMap(body));
                    return CafeUtils.message(CafeConstants.REGISTER_OK, HttpStatus.OK);
                } else {
                    return CafeUtils.message(CafeConstants.DUPLICATE_EMAIL, HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.message(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.deFault();
    }

    public ResponseEntity<JsonModel> login(LoginModel body) throws JSONException {
        log.info("Inside login {}", body);
        if (body.getEmail().length() == 0 && body.getPassword().length() == 0) {
            return CafeUtils.message("DON'T HAVE EMAIL OR PASSWORD", HttpStatus.BAD_REQUEST);
        }
        User u = userRepository.findByEmailId(body.getEmail());
        if (Objects.isNull(u)) {
            return CafeUtils.message("USER INVALID", HttpStatus.BAD_REQUEST);
        }
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            body.getEmail(), body.getPassword()
                    )
            );
            if (auth.isAuthenticated()) {
                if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    String token = jwtUtil.generateToken(
                            customerUserDetailsService.getUserDetail().getEmail(),
                            customerUserDetailsService.getUserDetail().getRole());
                    return CafeUtils.message(token
                            , HttpStatus.OK);
                } else {
                    return CafeUtils.message("Wait for admin approval.", HttpStatus.OK);
                }
            }
        } catch (Exception ex) {
            log.error("message {}", ex);
        }
        return CafeUtils.message("Bad Credentials.", HttpStatus.BAD_REQUEST);


    }

    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if (jwtFilter.isAdmin()) {
                log.info("isAdmin");
                return new ResponseEntity<>(userRepository.getAllUser(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public ResponseEntity<JsonModel> update(Map<String, String> body) throws JSONException {
        log.info("Inside updateUser");
        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> user = userRepository.findById(Integer.parseInt(body.get("id")));
                if (!user.isEmpty()) {
                    sendMailToAllAdmin(body.get("status"), user.get().getEmail(), userRepository.getAllAdmin());
                    userRepository.updateStatus(body.get("status"), Integer.parseInt(body.get("id")));
                    return CafeUtils.message("Update success", HttpStatus.OK);
                } else {
                    return CafeUtils.message("USER INVALID", HttpStatus.OK);
                }
            } else {
                return CafeUtils.message("NOT ADMIN", HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    private void sendMailToAllAdmin(String status, String email, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true")) {
            emailUtils.sendSimpleMessage(
                    email,
                    "Account Approved",
                    "USER :-" + email + " is approved\n ADMIN : " + jwtFilter.getCurrentUser(),
                    allAdmin
            );
        } else {
            emailUtils.sendSimpleMessage(
                    email,
                    "Account Disable",
                    "USER :-" + email + " is disable\n ADMIN : " + jwtFilter.getCurrentUser(),
                    allAdmin
            );

        }
    }

    public ResponseEntity<JsonModel> checkToken() throws JSONException {
        return CafeUtils.message("true", HttpStatus.OK);
    }

    public ResponseEntity<JsonModel> changPassword(Map<String, String> body) throws JSONException {
        try {
            User user = userRepository.findByEmailId(jwtFilter.getCurrentUser());

            if (!user.equals(null)) {
                if (bcryptEncoder.matches(body.get("oldPassword"), user.getPassword())) {
                    String newpass = bcryptEncoder.encode(body.get("newPassword"));
                    log.info("new pass{}", newpass);
                    user.setPassword(newpass);
                    userRepository.save(user);
                    return CafeUtils.message("PASSWORD UPDATE SUCCESS", HttpStatus.OK);
                }
                return CafeUtils.message("OLD PASSWORD NOT CORRECT", HttpStatus.BAD_REQUEST);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return CafeUtils.deFault();
    }

    public ResponseEntity<JsonModel> forgotPassword(Map<String, String> body) throws JSONException {

        log.info("email {}", body.get("email"));
        try {
            User user = userRepository.findByEmailId(body.get("email"));
            log.info("user {}", user);
            if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
                PasswordResetToken p = passwordResetTokenRepository.findByUserId(user);
                if (!Objects.isNull(p) && !Strings.isNullOrEmpty(p.getToken())) {
                    if (!isTokenExpired(p)) {
                        if (p.getStatus()) {
                            return CafeUtils.message(CafeConstants.RESET_PASSWORD_ALREADY, HttpStatus.OK);

                        } else {
                            log.info("duplicate");
                            emailUtils.sendResetPasswordEmail(user.getEmail(), CafeConstants.URL_RESET_PASSWORD + p.getToken());
                            return CafeUtils.message(CafeConstants.URL_RESET_PASSWORD + p.getToken(), HttpStatus.OK);

                        }
                    } else {
                        log.info("DELETE TOKEN");
                        deletePasswordResetToken(p);
                    }
                }
                String token = UUID.randomUUID().toString();
                createPasswordResetTokenForUser(user, token);
                String resetUrl = CafeConstants.URL_RESET_PASSWORD + token;
                emailUtils.sendResetPasswordEmail(user.getEmail(), resetUrl);
                return CafeUtils.message(resetUrl, HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();

    }

    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public void updatePassword(User user, String password) {
        user.setPassword(bcryptEncoder.encode(password));
        userRepository.save(user);
    }

    public void deletePasswordResetToken(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token);
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        log.info("cal {} expiryDate {}", cal.getTime(), passToken.getExpiryDate());
        return passToken.getExpiryDate().before(cal.getTime());
    }

    public ResponseEntity<JsonModel> processResetPassword(String token, String password) throws JSONException {
        log.info("Inside processResetPassword token {}", token);
        PasswordResetToken resetToken = getPasswordResetToken(token);
        log.info("Password {}", resetToken);

        try {
            if (resetToken == null) {
                return CafeUtils.message("Token Invalid", HttpStatus.BAD_REQUEST);
            }
            if (isTokenExpired(resetToken)) {
                return CafeUtils.message("Token is expired", HttpStatus.OK);

            }
            User user = resetToken.getUser();
            updatePassword(user, password);
            resetToken.setStatus(true);
            passwordResetTokenRepository.save(resetToken);
            return CafeUtils.message("Reset Password Success", HttpStatus.OK);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return CafeUtils.deFault();
    }

}
