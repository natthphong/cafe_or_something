package com.backendcafe.backend.controller;

import com.backendcafe.backend.constents.CafeConstants;
import com.backendcafe.backend.controllerImpl.UserControllerIMPL;
import com.backendcafe.backend.models.JsonModel;
import com.backendcafe.backend.models.LoginModel;
import com.backendcafe.backend.models.SignupModel;
import com.backendcafe.backend.service.UserService;
import com.backendcafe.backend.untils.CafeUtils;
import com.backendcafe.backend.wrapper.UserWrapper;

import com.google.common.base.Strings;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController implements UserControllerIMPL {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String hello() {
        return "U have token";
    }


    @Override
    public ResponseEntity<JsonModel> signUp(@RequestBody(required = true) SignupModel body) throws JSONException {
        try {
            return userService.signUp(body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    @Override
    public ResponseEntity<JsonModel> login(@RequestBody(required = true) LoginModel body) throws JSONException {
        log.info("Hello login {}", body);
        try {
            return userService.login(body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            return userService.getAllUser();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<JsonModel> update(@RequestBody(required = true) Map<String, String> body) throws JSONException {
        try {
            return userService.update(body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    @Override
    public ResponseEntity<JsonModel> checkToken() throws JSONException {
        try {
            return userService.checkToken();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    @Override
    public ResponseEntity<JsonModel> changPassword(@RequestBody(required = true) Map<String, String> body) throws JSONException {
        try {
            return userService.changPassword(body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    @Override
    public ResponseEntity<JsonModel> forgotPassword(@RequestBody(required = true) Map<String, String> body) throws JSONException {
        log.info("inside forgot");
        try {
            return userService.forgotPassword(body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }

    @Override
    public ResponseEntity<JsonModel> processResetPassword(@RequestParam(value = "token", required = true) String token,
                                                          @RequestBody(required = true) Map<String, String> body) throws JSONException {
        log.info("reset");
        try {
            String password = body.get("password");
            log.info("reset password {}", password);
            if (!Strings.isNullOrEmpty(body.get("password"))) {

                return userService.processResetPassword(token, password);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.deFault();
    }
}
