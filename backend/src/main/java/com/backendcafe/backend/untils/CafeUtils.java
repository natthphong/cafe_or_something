package com.backendcafe.backend.untils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class CafeUtils {

    private  CafeUtils(){

    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage , HttpStatus httpStatus)
    {
        return new ResponseEntity<String>("{\"message\" :\""+responseMessage +"\"}", httpStatus);
    }
}
