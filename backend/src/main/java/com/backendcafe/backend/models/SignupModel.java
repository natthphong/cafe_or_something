package com.backendcafe.backend.models;

import lombok.Data;

@Data
public class SignupModel {
    private String name;
    private String password;
    private String contactNumber;
    private String email;
}
