package com.backendcafe.backend.controllerImpl;

import com.backendcafe.backend.models.JsonModel;
import com.backendcafe.backend.models.LoginModel;
import com.backendcafe.backend.models.SignupModel;
import com.backendcafe.backend.wrapper.UserWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/user")
public interface UserControllerIMPL {
    @GetMapping("/test2")
    public String hello();

    @PostMapping("/login")
    public ResponseEntity<JsonModel> login(@RequestBody(required = true) LoginModel body) throws JSONException;

    @PostMapping("/signup")
    public ResponseEntity<JsonModel> signUp(@RequestBody(required = true) SignupModel body) throws JSONException;

    @GetMapping("/get")
    public ResponseEntity<List<UserWrapper>> getAllUser();
    @PutMapping("/update")
    public ResponseEntity<JsonModel> update(@RequestBody(required = true) Map<String, String> body) throws JSONException;

    @GetMapping("/checkToken")
    public ResponseEntity<JsonModel> checkToken() throws JSONException;

    @PostMapping("/changPassword")
    public ResponseEntity<JsonModel> changPassword(@RequestBody(required = true) Map<String, String> body) throws JSONException;

    @PostMapping("/forgotPassword")
    public ResponseEntity<JsonModel> forgotPassword(@RequestBody(required = true) Map<String, String> body) throws JSONException;

    @PostMapping("/reset-password")
    public ResponseEntity<JsonModel> processResetPassword(@RequestParam(value = "token", required = true) String token,
                                                       @RequestBody(required = true) Map<String, String> body) throws JSONException;
}
