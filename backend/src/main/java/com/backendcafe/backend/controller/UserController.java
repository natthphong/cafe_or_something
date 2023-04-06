package com.backendcafe.backend.controller;

import com.backendcafe.backend.constents.CafeConstants;
import com.backendcafe.backend.models.LoginModel;
import com.backendcafe.backend.models.SignupModel;
import com.backendcafe.backend.service.UserService;
import com.backendcafe.backend.untils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public String helloTest() {
        return "Hello Test";
    }

    @GetMapping("/test2")
    public String hello(){
        return  "U have token";
    }
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody(required = true) SignupModel body) {
        try {
            return userService.signUp(body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.DEFAULT_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) LoginModel body) {
            log.info("Hello login {}", body);
        try {
            return userService.login(body);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.DEFAULT_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
